package com.flynnd273.playtime.UiComponents

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.flynnd273.playtime.Navigation.Destination

@Composable
fun BottomNavigationBar(selectedDestination: Int, navController: NavHostController) {
	var selectedDestination1 = selectedDestination
	NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
		Destination.entries.filter { it.icon != null }.forEachIndexed { index, destination ->
			val builder = NavOptions.Builder()
			builder.setLaunchSingleTop(true)
			val navOptions = builder.build()
			NavigationBarItem(
				selected = index == selectedDestination1,
				onClick = {
					navController.navigate(route = destination.route, navOptions = navOptions)
					selectedDestination1 = index
				},
				icon = {
					Icon(
						imageVector = destination.icon!!,
						contentDescription = destination.contentDescription
					)
				},
				label = { Text(destination.label!!) }
			)
		}
	}
}
