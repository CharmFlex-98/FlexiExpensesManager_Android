package com.charmflex.flexiexpensesmanager.features.auth.service.sign_in

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.charmflex.flexiexpensesmanager.features.auth.storage.AuthStorage
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

internal class GoogleSignInService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authStorage: AuthStorage
) : SignInService {

    override suspend fun signIn(context: Context): SignInState {
        return withContext(Dispatchers.IO) {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("925930527769-3m3ca0jbfubrbc3qlc0crgii2ipa8008.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .setNonce("test nonce")
                .build()
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()


            val credentialManager = CredentialManager.create(context)
            try {
                val result = credentialManager.getCredential(
                    context,
                    request
                )
                val res = handleSignIn(result)

//                // Save
//                if (res is SignInState.Success) {
//                    authStorage.saveLoginID(res.uid)
//                }
                res
            } catch (e: Exception) {
                SignInState.Failure(e.message)
            }
        }
    }

    override suspend fun trySignIn(context: Context): SignInState {
        val currentUser = firebaseAuth.currentUser ?: return SignInState.Failure("User is not signed in")

        return SignInState.Success(
            currentUser.uid,
            currentUser.displayName,
            currentUser.email
        )
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): SignInState {
        // Handle the successfully returned credential.
        val credential = result.credential

        return when (credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                 if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                     try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        SignInState.Failure(e.message)
                    }
                } else {
                     SignInState.Failure(null)
                }
            }

            else -> {
                SignInState.Failure(null)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun firebaseAuthWithGoogle(idToken: String): SignInState {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign-in success
                        val user = firebaseAuth.currentUser
                        user?.let {
                            continuation.resume(SignInState.Success(user.uid, user.displayName, user.email))
                            return@addOnCompleteListener
                        }
                        continuation.resume(SignInState.Failure(null))
                    } else {
                        continuation.resume(SignInState.Failure(null))
                    }
                }

            // Handle cancellation (optional but recommended)
            continuation.invokeOnCancellation {
//                continuation.resume(SignInState.Failure("User Cancelled"))
            }
        }
    }
}