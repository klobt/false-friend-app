package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.agh.falsefriendapp.data.auth.LoginError
import org.agh.falsefriendapp.ui.components.BaseButton
import org.agh.falsefriendapp.ui.state.LoginUiState
import org.agh.falsefriendapp.ui.state.MAX_PASSWORD_LENGTH
import org.agh.falsefriendapp.ui.state.MIN_PASSWORD_LENGTH
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoggedIn: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onLoggedIn()
        }
    }

    LoginContent(
        state = state,
        onModeChange = viewModel::onModeChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordRepeatChange = viewModel::onPasswordRepeatChange,
        onSubmit = viewModel::onSubmit
    )
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    onModeChange: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordRepeatChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeader()
        Spacer(modifier = Modifier.height(24.dp))
        AuthModeSelector(
            isLoginSelected = state.isLoginMode,
            onSelectionChanged = onModeChange
        )
        Spacer(modifier = Modifier.height(32.dp))
        AuthForm(
            state = state,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onPasswordRepeatChange = onPasswordRepeatChange,
            onSubmit = onSubmit
        )
    }
}

@Composable
private fun AuthForm(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordRepeatChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val emailError = emailErrorMessage(state)
    val passwordError = passwordErrorMessage(state)
    val repeatError = passwordRepeatErrorMessage(state)

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("E-mail") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(emailError)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Hasło") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(passwordError)
                }
            }
        )

        if (!state.isLoginMode) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.passwordRepeat,
                onValueChange = onPasswordRepeatChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Powtórz hasło") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = repeatError != null,
                supportingText = {
                    if (repeatError != null) {
                        Text(repeatError)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.error != null) {
            Text(
                text = serverErrorMessage(state.error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier.height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            }
            else {
                BaseButton(
                    text = if (state.isLoginMode) "Zaloguj" else "Zarejestruj",
                    height = 56.dp,
                    enabled = state.canSubmit(),
                    onClick = onSubmit
                )
            }
        }
    }
}

private fun emailErrorMessage(state: LoginUiState): String? {
    return if (state.email.isNotBlank() && !state.isEmailValid()) {
        "Nieprawidłowy adres e-mail"
    }
    else {
        null
    }
}

private fun passwordErrorMessage(state: LoginUiState): String? {
    return when {
        state.isPasswordTooLong() ->
            "Hasło może mieć maksymalnie $MAX_PASSWORD_LENGTH znaki"
        !state.isLoginMode && state.password.isNotEmpty() && state.isPasswordTooShort() ->
            "Hasło musi mieć co najmniej $MIN_PASSWORD_LENGTH znaki"
        else -> null
    }
}

private fun passwordRepeatErrorMessage(state: LoginUiState): String? {
    return if (state.passwordRepeat.isNotEmpty() && !state.passwordsMatch()) {
        "Hasła nie są takie same"
    }
    else {
        null
    }
}

private fun serverErrorMessage(error: LoginError): String {
    return when (error) {
        LoginError.INVALID_CREDENTIALS -> "Nieprawidłowy e-mail lub hasło"
        LoginError.EMAIL_TAKEN -> "Ten e-mail jest już zarejestrowany"
        LoginError.INVALID_EMAIL -> "Nieprawidłowy adres e-mail"
        LoginError.NO_CONNECTION -> "Brak połączenia z serwerem"
        LoginError.UNKNOWN -> "Wystąpił błąd, spróbuj ponownie"
    }
}

@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "False Friend",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Nauka języka angielskiego",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun AuthModeSelector(
    isLoginSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
    ) {
        SegmentedButton(
            selected = isLoginSelected,
            onClick = { onSelectionChanged(true) },
            shape = RoundedCornerShape(
                topStart = 12.dp,
                bottomStart = 12.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            ),
            icon = {},
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = MaterialTheme.colorScheme.primary,
                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                inactiveContentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("Logowanie")
        }
        SegmentedButton(
            selected = !isLoginSelected,
            onClick = { onSelectionChanged(false) },
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                topEnd = 12.dp,
                bottomEnd = 12.dp
            ),
            icon = {},
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = MaterialTheme.colorScheme.primary,
                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                inactiveContainerColor = MaterialTheme.colorScheme.surface,
                inactiveContentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("Rejestracja")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    FalseFriendAppTheme {
        LoginContent(LoginUiState(), {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentErrorPreview() {
    FalseFriendAppTheme {
        LoginContent(
            LoginUiState(
                email = "jan@example.com",
                password = "tajne",
                error = LoginError.INVALID_CREDENTIALS
            ),
            {}, {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterContentPreview() {
    FalseFriendAppTheme {
        LoginContent(
            LoginUiState(isLoginMode = false, email = "jan@example.com"),
            {}, {}, {}, {}, {}
        )
    }
}
