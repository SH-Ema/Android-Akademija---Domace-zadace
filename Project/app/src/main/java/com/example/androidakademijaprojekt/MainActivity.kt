package com.example.androidakademijaprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidakademijaprojekt.ui.theme.AndroidAkademijaProjektTheme

data class MyData(
    val id: Int,
    val title: String,
    val description: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidAkademijaProjektTheme {

                var items by remember {
                    mutableStateOf(
                        listOf(
                            MyData(1, "Android Akademija", "Prvo"),
                            MyData(2, "Uvod u Kotlin", "Drugo"),
                            MyData(3, "Napredni Kotlin", "Treće"),
                            MyData(4, "Uvod u Android", "Četvrto"),
                            MyData(5, "Arhitektura", "Peto")
                        )
                    )
                }

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "list"
                ) {
                    composable("list") {
                        ListScreen(
                            items = items,
                            onShuffleClick = {
                                items = items.shuffled()
                            },
                            onAddClick = {
                                navController.navigate("edit/new")
                            },
                            onItemClick = { selectedItem ->
                                navController.navigate("edit/${selectedItem.id}")
                            }
                        )
                    }

                    composable(
                        route = "edit/{itemId}",
                        arguments = listOf(
                            navArgument("itemId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val itemIdText = backStackEntry.arguments?.getString("itemId")

                        EditNoteScreen(
                            itemIdText = itemIdText,
                            items = items,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onSaveClick = { id, title, description ->

                                if (id == null) {
                                    val newId = (items.maxOfOrNull { it.id } ?: 0) + 1

                                    items = items + MyData(
                                        id = newId,
                                        title = title,
                                        description = description
                                    )
                                } else {
                                    items = items.map { item ->
                                        if (item.id == id) {
                                            item.copy(
                                                title = title,
                                                description = description
                                            )
                                        } else {
                                            item
                                        }
                                    }
                                }

                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4C0F96)
    )
}

@Composable
fun DescriptionText(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontStyle = FontStyle.Italic,
        color = Color.Black,
        maxLines = 3
    )
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun ItemCard(
    item: MyData,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite icon",
                    tint = Color.Red,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Column {
                    TitleText(text = item.title)

                    Spacer(modifier = Modifier.height(10.dp))

                    DescriptionText(text = item.description)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row {
                CustomButton(text = "Fav")

                Spacer(modifier = Modifier.padding(5.dp))

                CustomButton(text = "Save")
            }
        }
    }
}

@Composable
fun MyItemList(
    items: List<MyData>,
    onItemClick: (MyData) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            ItemCard(
                item = item,
                onClick = {
                    onItemClick(item)
                }
            )
        }
    }
}

@Composable
fun ListScreen(
    items: List<MyData>,
    onShuffleClick: () -> Unit,
    onAddClick: () -> Unit,
    onItemClick: (MyData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(35.dp)
    ) {
        CustomButton(
            text = "Add note",
            onClick = onAddClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomButton(
            text = "Shuffle",
            onClick = onShuffleClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        MyItemList(
            items = items,
            onItemClick = onItemClick
        )
    }
}

@Composable
fun EditNoteScreen(
    itemIdText: String?,
    items: List<MyData>,
    onBackClick: () -> Unit,
    onSaveClick: (id: Int?, title: String, description: String) -> Unit
) {
    val itemId = itemIdText?.toIntOrNull()
    val existingItem = items.firstOrNull { it.id == itemId }

    var title by remember(itemIdText) {
        mutableStateOf(existingItem?.title ?: "")
    }

    var description by remember(itemIdText) {
        mutableStateOf(existingItem?.description ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        TitleText(
            text = if (existingItem == null) "Nova bilješka" else "Uredi bilješku"
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = title,
            onValueChange = { newTitle ->
                title = newTitle
            },
            label = {
                Text("Naslov")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = description,
            onValueChange = { newDescription ->
                description = newDescription
            },
            label = {
                Text("Opis")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Spremi",
            onClick = {
                onSaveClick(itemId, title, description)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomButton(
            text = "Back",
            onClick = onBackClick
        )
    }
}