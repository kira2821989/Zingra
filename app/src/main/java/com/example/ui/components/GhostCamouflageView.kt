package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZingraCyan

@Composable
fun GhostCamouflageView(
    onExitStealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayValue by remember { mutableStateOf("0") }
    var notesText by remember { mutableStateOf("Physics Assignment: Complete chapter 4 problems on wave kinematics & optics.\nSubmit before Friday 5:00 PM.") }
    var currentSubTab by remember { mutableStateOf(0) } // 0 = Calculator, 1 = Notes

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121418))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Discreet Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1D24))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculator",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "StudyCalc & Notes v2.4",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )
                }

                // Secret Exit Stealth button disguised as a mini power/battery bolt
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF242A35))
                        .clickable { onExitStealth() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("exit_stealth_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Exit Ghost Mode",
                            tint = ZingraCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(
                            text = "Exit Stealth",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZingraCyan
                        )
                    }
                }
            }

            // Sub tabs: Calculator vs Notes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (currentSubTab == 0) Color(0xFF2A313E) else Color(0xFF1E222B))
                        .clickable { currentSubTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Scientific Calculator",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (currentSubTab == 0) Color.White else Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (currentSubTab == 1) Color(0xFF2A313E) else Color(0xFF1E222B))
                        .clickable { currentSubTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Study Notes",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (currentSubTab == 1) Color.White else Color(0xFF94A3B8)
                    )
                }
            }

            if (currentSubTab == 0) {
                // Calculator Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color(0xFF0D0F13), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = displayValue,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Calculator Keypad
                val buttons = listOf(
                    listOf("C", "DEL", "%", "/"),
                    listOf("7", "8", "9", "*"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "±", "=")
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    buttons.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { label ->
                                val isOp = label in listOf("/", "*", "-", "+", "=")
                                val isClear = label in listOf("C", "DEL")
                                val bgColor = when {
                                    label == "=" -> Color(0xFF2563EB)
                                    isOp -> Color(0xFF334155)
                                    isClear -> Color(0xFF475569)
                                    else -> Color(0xFF1E2430)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(bgColor)
                                        .clickable {
                                            when (label) {
                                                "C" -> displayValue = "0"
                                                "DEL" -> {
                                                    displayValue = if (displayValue.length > 1) displayValue.dropLast(1) else "0"
                                                }
                                                "=" -> {
                                                    try {
                                                        // Simple eval demo
                                                        displayValue = "3.14159"
                                                    } catch (e: Exception) {
                                                        displayValue = "Error"
                                                    }
                                                }
                                                else -> {
                                                    displayValue = if (displayValue == "0") label else displayValue + label
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Study Notes Scratchpad
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Lecture Summary & Homework Scratchpad",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0D0F13),
                            unfocusedContainerColor = Color(0xFF0D0F13),
                            focusedBorderColor = Color(0xFF334155),
                            unfocusedBorderColor = Color(0xFF242A35),
                            focusedTextColor = Color(0xFFE2E8F0),
                            unfocusedTextColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer note explaining the stealth feature
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFF181C24), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Ghost Camouflage",
                        tint = ZingraCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "Ghost Shield Camouflage Active • Videos & chats hidden safely",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
