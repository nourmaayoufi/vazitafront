package com.attt.vazitaapp.util



/**
 * Application-wide constants
 */
object Constants {
    // API Endpoints
    const val BASE_URL = "https://api.inspection.com/"

    // API Endpoints
    const val ENDPOINT_LOGIN = "auth/login"
    const val ENDPOINT_REFRESH_TOKEN = "auth/refresh"
    const val ENDPOINT_USERS = "users"
    const val ENDPOINT_CAR_QUEUE = "inspection/queue"
    const val ENDPOINT_CHAPTERS = "inspection/chapters"
    const val ENDPOINT_POINTS = "inspection/points"
    const val ENDPOINT_ALTERATIONS = "inspection/alterations"
    const val ENDPOINT_SUBMIT_DEFECT = "inspection/submit"
    const val ENDPOINT_DEFECT_REVIEW = "defects/review"
    const val ENDPOINT_ANALYTICS = "analytics/dashboard"

    // SharedPreferences
    const val PREF_AUTH = "auth_preferences"
    const val PREF_TOKEN = "jwt_token"
    const val PREF_REFRESH_TOKEN = "refresh_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_ROLE = "user_role"
    const val PREF_CENTER_ID = "center_id"

    // User Roles
    const val ROLE_ADMIN = "ROLE_ADMIN"
    const val ROLE_INSPECTOR = "ROLE_INSPECTOR"
    const val ROLE_ADJOINT = "ROLE_ADJOINT"

    // Request Codes
    const val REQUEST_DEFECT_FORM = 100

    // Bundle Keys
    const val KEY_CAR_DOSSIER = "car_dossier"
    const val KEY_DEFECT_ID = "defect_id"
    const val KEY_USER_ID = "user_id"

    // Timeouts
    const val NETWORK_TIMEOUT = 30L
    const val SESSION_TIMEOUT = 30 * 60 * 1000L // 30 minutes

    // Pagination
    const val PAGE_SIZE = 20
}