package com.CargoTrack.cargotrack.Model

import com.google.android.gms.maps.model.LatLng

data class  Locations(
    val Johannesburg: LatLng = LatLng(-26.16926924102308, 28.22331865263266),
    val Durban: LatLng = LatLng(-29.736683887218078, 31.064520981645735),
    val EastLondon: LatLng = LatLng(-32.98548698382141, 27.862780953113802),
    val CapeTown: LatLng = LatLng(-33.87525178575219, 18.50860932698537),
    val DurbanDepo: LatLng = LatLng(-29.892733761786985, 31.017662181783287),
    val JoburgDepo: LatLng = LatLng(-26.20919248765956, 28.092595013618023),
    val PretoriaDepo: LatLng = LatLng(-25.71578989097838, 28.18248505459491),
    val CapeTownDepo: LatLng = LatLng(-33.818811994597475, 18.5286848954893),
    val PortElizabethDepo: LatLng = LatLng(-33.889335949813564, 25.61883452432908)
)