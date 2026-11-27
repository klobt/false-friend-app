package org.agh.falsefriendapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.data.auth.LoginError
import org.agh.falsefriendapp.ui.state.MAX_PASSWORD_LENGTH
import org.agh.falsefriendapp.ui.state.MIN_PASSWORD_LENGTH
import org.agh.falsefriendapp.ui.state.PasswordChangeUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme

@Composable
fun ChangePasswordDialog(
    state: PasswordChangeUiState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val serverError = serverErrorMessage(state.error)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zmiana hasła") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PasswordField(
                    value = state.currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = "Obecne hasło",
                    error = currentPasswordError(state),
                    imeAction = ImeAction.Next
                )
                PasswordField(
                    value = state.newPassword,
                    onValueChange = onNewPasswordChange,
                    label = "Nowe hasło",
                    error = newPasswordError(state),
                    imeAction = ImeAction.Next
                )
                PasswordField(
                    value = state.repeatPassword,
                    onValueChange = onRepeatPasswordChange,
                    label = "Powtórz nowe hasło",
                    error = repeatPasswordError(state),
                    imeAction = ImeAction.Done
                )
                if (serverError != null) {
                    Text(
                        text = serverError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = state.canSave()) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else {
            null
        }
    )
}

private fun currentPasswordError(state: PasswordChangeUiState): String? {
    return if (state.error == LoginError.INVALID_CREDENTIALS) {
        "Nieprawidłowe obecne hasło"
    } else {
        null
    }
}

private fun newPasswordError(state: PasswordChangeUiState): String? {
    return when {
        state.isNewPasswordTooLong() ->
            "Hasło może mieć maksymalnie $MAX_PASSWORD_LENGTH znaki"
        state.newPassword.isNotEmpty() && state.isNewPasswordTooShort() ->
            "Hasło musi mieć co najmniej $MIN_PASSWORD_LENGTH znaki"
        state.isSameAsCurrent() ->
            "Nowe hasło musi różnić się od obecnego"
        else -> null
    }
}

private fun repeatPasswordError(state: PasswordChangeUiState): String? {
    return if (state.repeatPassword.isNotEmpty() && !state.passwordsMatch()) {
        "Hasła nie są takie same"
    } else {
        null
    }
}

private fun serverErrorMessage(error: LoginError?): String? {
    return when (error) {
        null, LoginError.INVALID_CREDENTIALS -> null
        LoginError.NO_CONNECTION -> "Brak połączenia z serwerem"
        else -> "Wystąpił błąd, spróbuj ponownie"
    }
}

@Composable
private fun ChangePasswordDialogPreviewContent(state: PasswordChangeUiState) {
    FalseFriendAppTheme {
        ChangePasswordDialog(
            state = state,
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onRepeatPasswordChange = {},
            onSave = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordDialogEmptyPreview() {
    ChangePasswordDialogPreviewContent(PasswordChangeUiState())
}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordDialogValidationPreview() {
    ChangePasswordDialogPreviewContent(
        PasswordChangeUiState(
            currentPassword = "tajne",
            newPassword = "tajne",
            repeatPassword = "inne"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordDialogServerErrorPreview() {
    ChangePasswordDialogPreviewContent(
        PasswordChangeUiState(
            currentPassword = "zle",
            newPassword = "nowehaslo",
            repeatPassword = "nowehaslo",
            error = LoginError.INVALID_CREDENTIALS
        )
    )
}
