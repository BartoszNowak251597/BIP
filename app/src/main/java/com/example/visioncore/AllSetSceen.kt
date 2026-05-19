package com.example.visioncore

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caverock.androidsvg.SVG

@Composable
fun AllSetScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp, vertical = 28.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "◀",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable { onBackClick() }
            )

            Text(
                text = "All set !",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(54.dp))

        Text(
            text = "Your glasses are ready.\nYour profile and vision settings are all set, and you can start using them right away.",
            fontSize = 18.sp,
            lineHeight = 22.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        AllSetIllustration()

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .height(48.dp)
                    .width(170.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(26.dp))

                Text(
                    text = "▶",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AllSetIllustration() {
    Box(
        modifier = Modifier
            .width(220.dp)
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        SvgImage(
            svgCode = faceSvg,
            width = 192.dp,
            height = 229.dp,
            contentDescription = "VisionCore character",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 0.dp, y = 0.dp)
        )

        SvgImage(
            svgCode = redStarSvg,
            width = 42.dp,
            height = 39.dp,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 24.dp, y = 8.dp)
        )

        SvgImage(
            svgCode = blackStarSvg,
            width = 18.dp,
            height = 18.dp,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 68.dp)
        )

        SvgImage(
            svgCode = whiteStarSvg,
            width = 104.dp,
            height = 106.dp,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 6.dp, y = 10.dp)
        )
    }
}

@Composable
private fun SvgImage(
    svgCode: String,
    width: Dp,
    height: Dp,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val widthPx = with(density) { width.roundToPx() }.coerceAtLeast(1)
    val heightPx = with(density) { height.roundToPx() }.coerceAtLeast(1)

    val imageBitmap = remember(svgCode, widthPx, heightPx) {
        try {
            val svg = SVG.getFromString(svgCode)
            val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            svg.setDocumentWidth(widthPx.toFloat())
            svg.setDocumentHeight(heightPx.toFloat())
            svg.renderToCanvas(canvas)

            bitmap.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    imageBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier.size(width, height),
            contentScale = ContentScale.Fit
        )
    }
}

private val faceSvg = """
<svg width="192" height="229" viewBox="0 0 192 229" fill="none" xmlns="http://www.w3.org/2000/svg">
<path fill-rule="evenodd" clip-rule="evenodd" d="M21.2526 89.0925C21.2526 89.0925 19.2178 102.171 33.1335 100.614C47.0492 99.058 44.609 89.0925 44.609 89.0925C44.609 89.0925 29.9677 93.1347 21.2526 89.0925Z" fill="#FF302B" stroke="#FF1919" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path fill-rule="evenodd" clip-rule="evenodd" d="M75.6007 87.9502C75.6007 87.9502 76.6804 101.183 86.0927 100.743C95.5049 100.303 102.672 88.5881 100.016 89.0926C85.321 91.8837 75.6007 87.9502 75.6007 87.9502Z" fill="#FF302B" stroke="#FF1919" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path fill-rule="evenodd" clip-rule="evenodd" d="M87.4038 8.35759C87.4038 8.35759 127.073 42.1955 115.221 65.3729C103.368 88.5503 118.01 71.518 121.496 77.2327C124.982 82.9473 124.633 116.536 111.386 121.064C98.1394 125.593 117.007 126.72 109.992 138.044C101.053 152.473 65.3936 162.648 58.5493 161.405C55.1093 160.78 55.6913 171.932 88.4532 176.262C121.67 180.652 141.374 200.551 127.771 210.884C127.771 210.884 185.383 191.273 190.217 181.623L147.99 163.258C147.99 163.258 140.306 140.043 139.749 133.4C139.132 126.052 144.155 123.434 144.155 123.434C144.155 123.434 164.163 42.2364 141.853 19.5004C141.853 19.5004 119.625 5.72504 87.4038 8.35759Z" fill="#FF302B" stroke="#FF1919" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path fill-rule="evenodd" clip-rule="evenodd" d="M88.0569 155.319C88.0569 155.319 46.8615 183.835 22.3828 149.275C-0.32956 117.208 4.56942 59.1772 10.797 35.6138C14.0828 23.1812 22.3269 10.3219 36.4765 5.56358C57.8449 -1.62221 119.224 -0.191184 138.86 14.2198C159.229 29.1686 147.313 123.376 140.271 129.752C133.993 135.438 145.978 169.17 145.978 169.17L190.217 188.896C190.217 188.896 70.7263 276.618 1.40356 186.798C1.40356 186.798 27.8556 191.415 37.4331 188.057C47.0105 184.699 53.0801 172.343 53.0801 172.343C53.0801 172.343 78.0234 170.848 88.0569 155.319Z" stroke="black" stroke-width="4.16519" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path d="M50.3996 77.2329C50.3996 77.2329 37.3466 109.17 41.5857 112.727C45.8247 116.283 55.4183 112.727 55.4183 112.727C55.4183 112.727 62.5576 101.342 65.458 106.322C68.3583 111.303 66.3916 114.35 61.2396 112.727" stroke="#FF1919" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path fill-rule="evenodd" clip-rule="evenodd" d="M15.344 73.0464C15.344 73.0464 12.9037 89.8555 20.5731 92.9684C28.2423 96.0812 44.5848 92.5699 44.5848 92.5699L49.5071 74.6028C49.5071 74.6028 36.2602 66.8207 15.344 73.0464Z" fill="black" stroke="black" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path fill-rule="evenodd" clip-rule="evenodd" d="M71.5562 73.0464C71.5562 73.0464 69.1159 89.8555 76.7853 92.9684C84.4545 96.0812 102.582 92.9684 102.582 92.9684L105.719 74.6028C105.719 74.6028 92.4723 66.8207 71.5562 73.0464Z" fill="black" stroke="black" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path d="M47.7937 81.0896C47.7937 81.0896 56.6943 73.6857 71.1873 82.4921" stroke="black" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path fill-rule="evenodd" clip-rule="evenodd" d="M105.719 74.9625C105.719 74.9625 149.265 65.9976 151.72 68.3882C154.174 70.7789 155.959 73.9664 155.959 73.9664L153.951 74.9625C153.951 74.9625 153.058 69.8882 149.488 70.6323C145.919 71.3765 108.464 82.2522 105.158 82.4921" fill="black"/>
<path d="M105.719 74.9625C105.719 74.9625 149.265 65.9976 151.72 68.3882C154.174 70.7789 155.959 73.9664 155.959 73.9664L153.951 74.9625C153.951 74.9625 153.058 69.8882 149.488 70.6323C145.919 71.3765 108.464 82.2522 105.158 82.4921" stroke="black" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
<path d="M29.5378 123.434C29.5378 123.434 58.3494 147.362 89.3457 121.064" stroke="#FF1919" stroke-width="4.16155" stroke-miterlimit="1.5" stroke-linecap="round" stroke-linejoin="round"/>
</svg>
""".trimIndent()

private val whiteStarSvg = """
<svg width="114" height="117" viewBox="0 0 114 117" fill="none" xmlns="http://www.w3.org/2000/svg">
<path d="M74.588 1.6083C75.1896 1.85009 75.5681 2.45158 75.5257 3.09863L72.8394 44.0977L111.269 59.5406C111.873 59.7834 112.251 60.3878 112.206 61.0371C112.16 61.6864 111.701 62.2323 111.069 62.3885L70.9931 72.2869L68.3077 113.276C68.2652 113.924 67.8111 114.471 67.1819 114.632C66.5526 114.792 65.8916 114.528 65.5442 113.98L43.4523 79.0883L3.35976 88.9907C2.7293 89.1464 2.06972 88.8792 1.72637 88.328C1.38315 87.7768 1.4341 87.0674 1.85177 86.5702L28.2751 55.1205L6.19321 20.2436C5.8451 19.6936 5.8913 18.9817 6.30773 18.4815C6.72422 17.9812 7.41596 17.807 8.01994 18.0497L46.4491 33.4926L72.8798 2.03536C73.2969 1.5389 73.9863 1.36652 74.588 1.6083Z" fill="white" stroke="black" stroke-width="3" stroke-linejoin="round"/>
</svg>
""".trimIndent()

private val redStarSvg = """
<svg width="51" height="47" viewBox="0 0 51 47" fill="none" xmlns="http://www.w3.org/2000/svg">
<path d="M25.35 1.5C25.9926 1.5 26.5644 1.90912 26.7709 2.51758L31.7006 17.0469H47.6996C48.3552 17.0469 48.9351 17.4731 49.1313 18.0986C49.3272 18.724 49.0941 19.4041 48.5561 19.7783L35.6694 28.7422L40.5834 43.2207C40.7916 43.8341 40.5824 44.5119 40.0649 44.9014C39.5473 45.2908 38.8378 45.3045 38.3061 44.9346L25.35 35.9209L12.394 44.9346C11.8622 45.3045 11.1528 45.2908 10.6352 44.9014C10.1176 44.5119 9.90848 43.8341 10.1166 43.2207L15.0297 28.7422L2.14398 19.7783C1.60596 19.4041 1.37285 18.724 1.56879 18.0986C1.76494 17.4731 2.34491 17.0469 3.00043 17.0469H18.9995L23.9291 2.51758L23.9721 2.40625C24.2074 1.85974 24.7475 1.5 25.35 1.5Z" fill="#FF302B" stroke="black" stroke-width="3" stroke-linejoin="round"/>
</svg>
""".trimIndent()

private val blackStarSvg = """
<svg width="21" height="21" viewBox="0 0 21 21" fill="none" xmlns="http://www.w3.org/2000/svg">
<path d="M1.79516 0.442709L8.95599 4.9728L14.4911 5.23212e-05L13.3816 7.77255L20.5425 12.3026L12.6959 12.5762L11.5865 20.3487L7.84653 12.7453L2.19336e-05 13.0189L5.53512 8.04613L1.79516 0.442709Z" fill="black"/>
</svg>
""".trimIndent()