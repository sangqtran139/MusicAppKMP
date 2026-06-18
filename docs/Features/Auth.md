# Feature: Auth

Module: `:feature:auth` — package `com.sangtq.musicappkmp.feature.auth`

## Purpose

Quản lý đăng nhập và session: lấy/lưu/refresh token, gate truy cập toàn app. Là feature duy nhất
được phép ghi token vào `SecureStorage`.

## Business Flow

- Người dùng đăng nhập bằng email/password → backend trả `access` + `refresh` token.
- Token lưu vào `SecureStorage` (Keystore/Keychain). Mọi request sau gắn Bearer tự động (plugin Auth của Ktor).
- 401 → plugin Auth tự refresh; refresh thất bại → xóa token, buộc đăng nhập lại.
- Logout → xóa token + dữ liệu phiên.

## User Flow

```
Splash ─▶ có token hợp lệ? ── có ─▶ Home
                          └─ không ─▶ Login ─▶ (submit) ─▶ Home
```

## Screens

- `LoginScreen` (stateful) + `LoginContent` (stateless): form email/password, trạng thái loading/error.
- (Tùy chọn) `SplashScreen`: kiểm tra session khi mở app.

## Navigation Flow

- Route: `@Serializable data object LoginRoute`.
- `NavGraphBuilder.authGraph(onLoggedIn: () -> Unit)`; wire ở `:shared`. Đăng nhập thành công → `Effect.NavigateToHome` → `onLoggedIn()`.

## State Flow

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface LoginIntent : Intent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
}

sealed interface LoginEffect : Effect {
    data object NavigateToHome : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}
```

UseCase: `LoginUseCase`, `ObserveSessionUseCase`, `LogoutUseCase`.

## API Used

- `POST /auth/login` → `{ access, refresh }`
- `POST /auth/refresh` (dùng bởi plugin Auth qua `refreshClient`)
- `POST /auth/logout`

## Related Modules

- `core:network` (Auth/refresh, HttpClient), `core:datastore` (`SecureStorage`), `core:ui` (MVI base), `:shared` (đăng ký module + navigation gate).

## Known Issues

- Chưa có social login / biometric unlock (giai đoạn sau).
- Cần xử lý đồng bộ nhiều request 401 cùng lúc (refresh một lần, các request chờ) — kiểm tra hành vi plugin Auth.
