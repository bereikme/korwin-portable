/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package dev.khenzii.korwin_portable.presentation

import android.os.Bundle
import android.view.InputDevice
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import dev.khenzii.korwin_portable.presentation.theme.KorwinportableTheme
import dev.khenzii.Korwin
import dev.khenzii.korwin_portable.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.ranges.coerceIn

class MainActivity : ComponentActivity() {
    private var boxScrollState: ScrollState = ScrollState(0)
    private var boxCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var currentScrollValue: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            App(Korwin.generateStatement())
        }
    }

    // Scroll quote text on bezel turn.
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_SCROLL && event.isFromSource(InputDevice.SOURCE_ROTARY_ENCODER)) {
            val scrollValue = event.getAxisValue(MotionEvent.AXIS_SCROLL)
            val relativeScrollValue = (scrollValue * 50).toInt()
            
            currentScrollValue -= relativeScrollValue
            currentScrollValue = currentScrollValue.coerceIn(0, boxScrollState.maxValue)

            boxCoroutineScope.launch {
                boxScrollState.scrollTo(currentScrollValue)
            }

            return true
        }
        return super.onGenericMotionEvent(event)
    }

    @Composable
    fun App(initialText: String = "") {
        KorwinportableTheme {
            boxScrollState = rememberScrollState()
            boxCoroutineScope = rememberCoroutineScope()
               
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.korwin),
                    contentDescription = "A high quality image of Janusz Korwin-Mikke",
                )

                Box(
                    modifier = Modifier
                        .padding(50.dp)
                        .verticalScroll(boxScrollState),
                    contentAlignment = Alignment.Center,
                ) {
                    Quote(initialText)
                }
            }
        }
    }
}

@Composable
fun Quote(content: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = content,
        fontSize = 20.sp,
    )
}
