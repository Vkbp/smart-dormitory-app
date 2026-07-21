package com.ktx.dormitory.shared.notification.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueReportBottomSheet(
    onDismiss: () -> Unit,
    viewModel: IssueReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is IssueReportUiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                IssueReportUiEffect.DismissSheet -> onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Báo hỏng / Sự cố",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onEvent(IssueReportUiEvent.DescriptionChanged(it)) },
                label = { Text("Mô tả sự cố") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Ví dụ: Vòi nước bị gãy, bóng đèn bị cháy...") },
                supportingText = {
                    Text("Mô tả chi tiết để kỹ thuật viên xử lý nhanh hơn.")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox khu vực chung
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = uiState.isCommonArea,
                        onValueChange = { viewModel.onEvent(IssueReportUiEvent.IsCommonAreaChanged(it)) },
                        role = Role.Checkbox
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.isCommonArea,
                    onCheckedChange = null // null because the row handles the click
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Sự cố tại khu vực chung",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Hành lang, thang máy, nhà xe, v.v.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onEvent(IssueReportUiEvent.SubmitReport) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isLoading && uiState.description.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("GỬI BÁO CÁO", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
