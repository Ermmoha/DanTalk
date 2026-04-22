package com.example.feature.main.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.core.design.theme.DanTalkTheme
import com.example.core.network_observer.ConnectionState
import com.example.core.network_observer.connectionState
import com.example.core.ui.components.NoInternetConnection
import com.example.core.ui.components.snackbar.CustomSnackbarHost
import com.example.core.ui.components.topbar.ExtraTopBar
import com.example.core.ui.model.UiUserData
import com.example.core.ui.util.InputFormField
import com.example.dantalk.features.main.profile.component.ProfileComponent
import com.example.feature.main.profile.store.ProfileStore
import com.example.feature.main.profile.ui.components.ProfileBottomSheet
import com.example.feature.main.profile.util.ProfileValidation

@Composable
fun ProfileContent(
    component: ProfileComponent,
) {
    val state by component.state.collectAsState()

    Content(
        state = state,
        onIntent = component::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: ProfileStore.State,
    onIntent: (ProfileStore.Intent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val connectionState by connectionState()

    val sheetState = rememberModalBottomSheetState(true)
    var sheetIsVisible by remember { mutableStateOf(false) }

    val fields = listOf(
        InputFormField(
            title = "Имя",
            value = state.newUserData.firstname,
            onValueChange = {
                onIntent(ProfileStore.Intent.UpdateNewUserData(state.newUserData.copy(firstname = it)))
            },
            isError = state.validation == ProfileValidation.EmptyFirstname,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        ),
        InputFormField(
            title = "Фамилия",
            value = state.newUserData.lastname,
            onValueChange = {
                onIntent(ProfileStore.Intent.UpdateNewUserData(state.newUserData.copy(lastname = it)))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        ),
        InputFormField(
            title = "Отчество",
            value = state.newUserData.patronymic,
            onValueChange = {
                onIntent(ProfileStore.Intent.UpdateNewUserData(state.newUserData.copy(patronymic = it)))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        ),
        InputFormField(
            title = "Электронная почта",
            value = state.newUserData.email,
            onValueChange = {
                onIntent(ProfileStore.Intent.UpdateNewUserData(state.newUserData.copy(email = it)))
            },
            isError = state.validation == ProfileValidation.EmptyEmail
                    || state.validation == ProfileValidation.InvalidEmailFormat
                    || state.validation == ProfileValidation.EmailExists,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        ),
        InputFormField(
            title = "Имя пользователя",
            value = state.newUserData.username,
            onValueChange = {
                onIntent(ProfileStore.Intent.UpdateNewUserData(state.newUserData.copy(username = it)))
            },
            isError = state.validation == ProfileValidation.EmptyUsername
                    || state.validation == ProfileValidation.UsernameExists,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )
    )

    LaunchedEffect(state.validation) {
        if (state.validation != ProfileValidation.Valid) {
            snackbarHostState.showSnackbar(
                message = when (state.validation) {
                    ProfileValidation.EmptyFirstname -> "Поле имени не должно быть пустым"
                    ProfileValidation.EmptyEmail -> "Поле электронной почты не должно быть пустым"
                    ProfileValidation.InvalidEmailFormat -> "Неверный формат электронной почты"
                    ProfileValidation.EmptyUsername -> "Поле имени пользователя не должно быть пустым"
                    ProfileValidation.UsernameExists -> "Имя пользователя уже существует"
                    ProfileValidation.EmailExists -> "Электронная почта уже существует"
                    ProfileValidation.Valid -> ""
                }
            )
        }
    }

    val photoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) onIntent(ProfileStore.Intent.LoadImageIntoStorage(context, uri))
        }

    Scaffold(
        topBar = {
            Column {
                ExtraTopBar(
                    actionsIcon = Icons.Default.Image,
                    actions = { photoLauncher.launch("image/*") },
                    navigateBack = { onIntent(ProfileStore.Intent.NavigateBack) },
                    color = Color.White
                )
                CustomSnackbarHost(snackbarHostState)
            }
        },
        floatingActionButton = {
            ProfileEditFloatingButton { sheetIsVisible = true }
        },
        containerColor = DanTalkTheme.colors.singleTheme
    ) { contentPadding ->
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ProfileAvatar(state.currentUser ?: UiUserData())
                ProfileInformation(
                    email = state.currentUser?.email ?: "",
                    username = state.currentUser?.username ?: "",
                )
                if (sheetIsVisible)
                    ProfileBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = { sheetIsVisible = false },
                        onClick = {
                            onIntent(ProfileStore.Intent.SaveNewUserData)
                            sheetIsVisible = false
                        },
                        profileFormFields = fields,
                    )
            }
            if (connectionState !is ConnectionState.Available)
                NoInternetConnection(
                    connectionState = connectionState,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(contentPadding)
                )
        }
    }
}

@Composable
private fun ProfileAvatar(
    user: UiUserData,
) {
    val name = if ((user.lastname.isBlank() && user.patronymic.isBlank()))
        user.firstname
    else if (user.patronymic.isBlank())
        user.firstname + "\n" + user.lastname
    else if (user.lastname.isBlank())
        user.firstname + "\n" + user.patronymic
    else
        user.firstname + "\n" + user.lastname + "\n" + user.patronymic
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp)
    ) {
        AsyncImage(
            model = user.avatar,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                color = if (isSystemInDarkTheme())
                    Color(211, 211, 211, 255)
                else
                    Color.White,
                blendMode = BlendMode.Darken
            )
        )
        Text(
            text = name,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.7f),
                    blurRadius = 100f
                )
            )
        )
    }
}

@Composable
private fun ProfileInformation(
    email: String,
    username: String,
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Информация",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DanTalkTheme.colors.main
        )
        InformationItem(
            icon = Icons.Outlined.Email,
            title = "Электронная почта",
            text = email
        )
        InformationItem(
            icon = Icons.Outlined.Person,
            title = "Имя пользователя",
            text = username
        )
    }
}

@Composable
private fun InformationItem(
    icon: ImageVector,
    title: String,
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DanTalkTheme.colors.hint
        )
        VerticalDivider(
            modifier = Modifier
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp)),
            thickness = 1.25f.dp,
            color = DanTalkTheme.colors.hint
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = DanTalkTheme.colors.oppositeTheme
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = DanTalkTheme.colors.hint
            )
        }
    }
}

@Composable
private fun ProfileEditFloatingButton(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        containerColor = DanTalkTheme.colors.main,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}
