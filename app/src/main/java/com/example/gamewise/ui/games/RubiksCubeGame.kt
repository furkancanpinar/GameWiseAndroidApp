package com.example.gamewise.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.*

enum class CubeFace { U, D, L, R, F, B }

val FaceColors = mapOf(
    CubeFace.U to Color.White,
    CubeFace.D to Color.Yellow,
    CubeFace.L to Color(0xFFFFA500), // Orange
    CubeFace.R to Color.Red,
    CubeFace.F to Color.Green,
    CubeFace.B to Color.Blue,
)

@Composable
fun RubiksCubeGame() {
    var cubeState by remember { mutableStateOf(initialCubeState()) }
    var rotationX by remember { mutableFloatStateOf(-25f) }
    var rotationY by remember { mutableFloatStateOf(45f) }
    var is3DView by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Rubik's Cube ${if (is3DView) "3D" else "2D"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = { is3DView = !is3DView }) {
                Text(if (is3DView) "Show 2D Layout" else "Show 3D View")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (is3DView) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            rotationY += dragAmount.x * 0.5f
                            rotationX -= dragAmount.y * 0.5f
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                RubiksCube3DCanvas(cubeState, rotationX, rotationY)
            }
            Text(
                "Drag to rotate the cube",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        } else {
            CubeUnfoldedView(cubeState)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Controls
        Text("Moves", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        
        val moves = listOf("U", "D", "L", "R", "F", "B")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 3
        ) {
            moves.forEach { move ->
                Button(
                    onClick = { cubeState = performMove(cubeState, move) },
                    modifier = Modifier.padding(4.dp).width(60.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(move)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { 
                cubeState = initialCubeState()
                rotationX = -25f
                rotationY = 45f
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Reset Cube")
        }
    }
}

@Composable
fun RubiksCube3DCanvas(state: Map<CubeFace, List<Color>>, rotX: Float, rotY: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val scale = min(size.width, size.height) * 0.4f

        val rx = Math.toRadians(rotX.toDouble())
        val ry = Math.toRadians(rotY.toDouble())

        // Define stickers for each face
        val stickers = mutableListOf<Sticker3D>()
        CubeFace.entries.forEach { face ->
            val faceStickers = state[face]!!
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    stickers.add(createSticker(face, i, j, faceStickers[(i * 3) + j]))
                }
            }
        }

        // Project and sort by depth
        val projected = stickers.map { sticker ->
            val points = sticker.points.map { p -> project(p, rx, ry, scale, centerX, centerY) }
            val depth = sticker.points.map { p -> getZ(p, rx, ry) }.average()
            ProjectedSticker(points, sticker.color, depth)
        }.sortedBy { it.depth }

        // Draw stickers
        projected.forEach { s ->
            // Simple visibility test: check if the face is pointing towards us
            // Actually sorting by depth is enough for orthographic projection of a convex cube
            val path = Path().apply {
                moveTo(s.points[0].x, s.points[0].y)
                lineTo(s.points[1].x, s.points[1].y)
                lineTo(s.points[2].x, s.points[2].y)
                lineTo(s.points[3].x, s.points[3].y)
                close()
            }
            drawPath(path, s.color, style = Fill)
            drawPath(path, Color.Black, style = Stroke(width = 2f))
        }
    }
}

data class Point3D(val x: Float, val y: Float, val z: Float)
data class Sticker3D(val points: List<Point3D>, val color: Color)
data class ProjectedSticker(val points: List<Offset>, val color: Color, val depth: Double)

fun createSticker(face: CubeFace, row: Int, col: Int, color: Color): Sticker3D {
    val r = row - 1.5f
    val c = col - 1.5f
    
    val points = when (face) {
        CubeFace.U -> listOf(
            Point3D(c, 1.5f, -r), Point3D(c + 1, 1.5f, -r),
            Point3D(c + 1, 1.5f, -(r + 1)), Point3D(c, 1.5f, -(r + 1))
        )
        CubeFace.D -> listOf(
            Point3D(c, -1.5f, r), Point3D(c + 1, -1.5f, r),
            Point3D(c + 1, -1.5f, r + 1), Point3D(c, -1.5f, r + 1)
        )
        CubeFace.L -> listOf(
            Point3D(-1.5f, -r, -c), Point3D(-1.5f, -r, -(c + 1)),
            Point3D(-1.5f, -(r + 1), -(c + 1)), Point3D(-1.5f, -(r + 1), -c)
        )
        CubeFace.R -> listOf(
            Point3D(1.5f, -r, c), Point3D(1.5f, -r, c + 1),
            Point3D(1.5f, -(r + 1), c + 1), Point3D(1.5f, -(r + 1), c)
        )
        CubeFace.F -> listOf(
            Point3D(c, -r, 1.5f), Point3D(c + 1, -r, 1.5f),
            Point3D(c + 1, -(r + 1), 1.5f), Point3D(c, -(r + 1), 1.5f)
        )
        CubeFace.B -> listOf(
            Point3D(-c, -r, -1.5f), Point3D(-(c + 1), -r, -1.5f),
            Point3D(-(c + 1), -(r + 1), -1.5f), Point3D(-c, -(r + 1), -1.5f)
        )
    }
    return Sticker3D(points, color)
}

fun project(p: Point3D, rx: Double, ry: Double, scale: Float, cx: Float, cy: Float): Offset {
    // Rotate Y
    val x1 = p.x * cos(ry) + p.z * sin(ry)
    val z1 = -p.x * sin(ry) + p.z * cos(ry)
    // Rotate X
    val y2 = p.y * cos(rx) - z1 * sin(rx)
    
    return Offset((x1.toFloat() * scale) + cx, (-y2.toFloat() * scale) + cy)
}

fun getZ(p: Point3D, rx: Double, ry: Double): Double {
    val z1 = -p.x * sin(ry) + p.z * cos(ry)
    return p.y * sin(rx) + z1 * cos(rx)
}

@Composable
fun CubeUnfoldedView(state: Map<CubeFace, List<Color>>) {
    val size = 30.dp
    val spacing = 2.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Up Face
        Row {
            Spacer(modifier = Modifier.size((size * 3) + (spacing * 6)))
            FaceGrid(state[CubeFace.U]!!, size)
            Spacer(modifier = Modifier.size((size * 6) + (spacing * 12)))
        }
        
        Spacer(modifier = Modifier.height(spacing))

        // Middle Row: Left, Front, Right, Back
        Row {
            FaceGrid(state[CubeFace.L]!!, size)
            Spacer(modifier = Modifier.width(spacing))
            FaceGrid(state[CubeFace.F]!!, size)
            Spacer(modifier = Modifier.width(spacing))
            FaceGrid(state[CubeFace.R]!!, size)
            Spacer(modifier = Modifier.width(spacing))
            FaceGrid(state[CubeFace.B]!!, size)
        }

        Spacer(modifier = Modifier.height(spacing))

        // Down Face
        Row {
            Spacer(modifier = Modifier.size((size * 3) + (spacing * 6)))
            FaceGrid(state[CubeFace.D]!!, size)
            Spacer(modifier = Modifier.size((size * 6) + (spacing * 12)))
        }
    }
}

@Composable
fun FaceGrid(faceColors: List<Color>, size: androidx.compose.ui.unit.Dp) {
    Column(modifier = Modifier.border(1.dp, Color.Black)) {
        for (i in 0 until 3) {
            Row {
                for (j in 0 until 3) {
                    Box(
                        modifier = Modifier
                            .size(size)
                            .background(faceColors[i * 3 + j])
                            .border(0.5.dp, Color.Black.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

fun initialCubeState(): Map<CubeFace, List<Color>> {
    return CubeFace.entries.associateWith { face ->
        List(9) { FaceColors[face]!! }
    }
}

fun performMove(state: Map<CubeFace, List<Color>>, move: String): Map<CubeFace, List<Color>> {
    val newState = state.toMutableMap().mapValues { it.value.toMutableList() }

    fun rotateFace(face: CubeFace) {
        val f = newState[face]!!
        val oldF = f.toList()
        f[0] = oldF[6]; f[1] = oldF[3]; f[2] = oldF[0]
        f[3] = oldF[7]; f[4] = oldF[4]; f[5] = oldF[1]
        f[6] = oldF[8]; f[7] = oldF[5]; f[8] = oldF[2]
    }

    when (move) {
        "U" -> {
            rotateFace(CubeFace.U)
            val f = newState[CubeFace.F]!!; val r = newState[CubeFace.R]!!
            val b = newState[CubeFace.B]!!; val l = newState[CubeFace.L]!!
            val temp = listOf(f[0], f[1], f[2])
            f[0] = r[0]; f[1] = r[1]; f[2] = r[2]
            r[0] = b[0]; r[1] = b[1]; r[2] = b[2]
            b[0] = l[0]; b[1] = l[1]; b[2] = l[2]
            l[0] = temp[0]; l[1] = temp[1]; l[2] = temp[2]
        }
        "D" -> {
            rotateFace(CubeFace.D)
            val f = newState[CubeFace.F]!!; val r = newState[CubeFace.R]!!
            val b = newState[CubeFace.B]!!; val l = newState[CubeFace.L]!!
            val temp = listOf(f[6], f[7], f[8])
            f[6] = l[6]; f[7] = l[7]; f[8] = l[8]
            l[6] = b[6]; l[7] = b[7]; l[8] = b[8]
            b[6] = r[6]; b[7] = r[7]; b[8] = r[8]
            r[6] = temp[0]; r[7] = temp[1]; r[8] = temp[2]
        }
        "L" -> {
            rotateFace(CubeFace.L)
            val u = newState[CubeFace.U]!!; val f = newState[CubeFace.F]!!
            val d = newState[CubeFace.D]!!; val b = newState[CubeFace.B]!!
            val temp = listOf(u[0], u[3], u[6])
            u[0] = b[8]; u[3] = b[5]; u[6] = b[2]
            b[8] = d[0]; b[5] = d[3]; b[2] = d[6]
            d[0] = f[0]; d[3] = f[3]; d[6] = f[6]
            f[0] = temp[0]; f[3] = temp[1]; f[6] = temp[2]
        }
        "R" -> {
            rotateFace(CubeFace.R)
            val u = newState[CubeFace.U]!!; val f = newState[CubeFace.F]!!
            val d = newState[CubeFace.D]!!; val b = newState[CubeFace.B]!!
            val temp = listOf(u[2], u[5], u[8])
            u[2] = f[2]; u[5] = f[5]; u[8] = f[8]
            f[2] = d[2]; f[5] = d[5]; f[8] = d[8]
            d[2] = b[6]; d[5] = b[3]; d[8] = b[0]
            b[6] = temp[0]; b[3] = temp[1]; b[0] = temp[2]
        }
        "F" -> {
            rotateFace(CubeFace.F)
            val u = newState[CubeFace.U]!!; val r = newState[CubeFace.R]!!
            val d = newState[CubeFace.D]!!; val l = newState[CubeFace.L]!!
            val temp = listOf(u[6], u[7], u[8])
            u[6] = l[8]; u[7] = l[5]; u[8] = l[2]
            l[8] = d[2]; l[5] = d[1]; l[2] = d[0]
            d[2] = r[0]; d[1] = r[3]; d[0] = r[6]
            r[0] = temp[0]; r[3] = temp[1]; r[6] = temp[2]
        }
        "B" -> {
            rotateFace(CubeFace.B)
            val u = newState[CubeFace.U]!!; val r = newState[CubeFace.R]!!
            val d = newState[CubeFace.D]!!; val l = newState[CubeFace.L]!!
            val temp = listOf(u[0], u[1], u[2])
            u[0] = r[2]; u[1] = r[5]; u[2] = r[8]
            r[2] = d[8]; r[5] = d[7]; r[8] = d[6]
            d[8] = l[6]; d[7] = l[3]; d[6] = l[0]
            l[6] = temp[0]; l[3] = temp[1]; l[0] = temp[2]
        }
    }

    return newState.mapValues { it.value.toList() }
}
