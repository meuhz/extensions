package com.example.demo.activity.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.demo.R
import com.example.demo.activity.compose.theme.MyApplicationTheme

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {

    MyApplicationTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            Modifier.fillMaxSize(),
            color = colorScheme.background
        ) {
            Column(
                Modifier
                    .padding(top = 64.dp)
                    .border(1.dp, Color.Blue)
            ) {
                val context = LocalContext.current
                IconButton(
                    modifier = Modifier
                        .padding(20.dp)
                        .requiredWidth(150.dp)
                        .border(1.dp, Color.Green),
                    onClick = {
                        Toast.makeText(context, "ComposeActivity", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.DarkGray)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_dashboard),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .size(24.dp)
                        )
                        Text("text")
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(0.5.dp)
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                )

                Spacer(
                    modifier = Modifier
                        .height(Dp.Hairline)
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                )
            }
        }
    }
}


@Composable
fun IconRow(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(42.dp)
            .fillMaxWidth()
            .background(
                color = if (isPressed) Color.LightGray else Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .size(24.dp)
        )
        Text(
            text,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun InteractionSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    color: Color = Color.Transparent,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        content()
    }
}