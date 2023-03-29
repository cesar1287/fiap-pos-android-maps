package com.github.cesar1287.mapsandroid.model

import com.google.android.gms.common.api.ResolvableApiException

sealed class LocationError {
    object ErrorLocationUnavailable : LocationError()
    data class GpsDisabled(val exception: ResolvableApiException) : LocationError()
    object GpsSettingUnavailable : LocationError()
}
