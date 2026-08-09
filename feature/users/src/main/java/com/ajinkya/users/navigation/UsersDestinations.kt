package com.ajinkya.users.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ajinkya.users.detail.UserDetailScreen
import com.ajinkya.users.list.UsersListScreen

object UsersDestinations {
    const val USER_ID_ARG = "userId"
    const val LIST_ROUTE = "users_list"
    const val DETAIL_ROUTE = "user_detail"
    const val DETAIL_ROUTE_WITH_ARG = "$DETAIL_ROUTE/{$USER_ID_ARG}"

    fun detailRoute(userId: Int): String = "$DETAIL_ROUTE/$userId"
}

/**
 * Registers the users list + detail destinations. The host app owns the [NavController]
 * and simply plugs this graph into its own NavHost.
 */
fun NavGraphBuilder.usersGraph(navController: NavController) {
    composable(route = UsersDestinations.LIST_ROUTE) {
        UsersListScreen(
            onUserClick = { userId ->
                navController.navigate(UsersDestinations.detailRoute(userId))
            },
        )
    }
    composable(
        route = UsersDestinations.DETAIL_ROUTE_WITH_ARG,
        arguments = listOf(
            navArgument(UsersDestinations.USER_ID_ARG) { type = NavType.IntType },
        ),
    ) {
        UserDetailScreen(
            onBackClick = { navController.popBackStack() },
        )
    }
}

