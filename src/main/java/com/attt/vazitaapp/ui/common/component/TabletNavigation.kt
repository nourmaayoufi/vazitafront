package com.attt.vazitaapp.ui.common.component


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.forEach
import com.google.android.material.navigation.NavigationView
import com.attt.vazitaapp.R

/**
 * Custom navigation component optimized for tablets.
 * Provides a side navigation drawer that's always visible in landscape mode.
 */
class TabletNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val navigationView: NavigationView
    private var navigationItemSelectedListener: ((MenuItem) -> Boolean)? = null
    private var currentSelectedItemId: Int = -1

    init {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.view_tablet_navigation, this, true)

        // Initialize navigation view
        navigationView = findViewById(R.id.navigationView)

        // Set navigation item click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Update current selected item
            currentSelectedItemId = menuItem.itemId

            // Update selection state
            updateSelectionState()

            // Notify listener
            navigationItemSelectedListener?.invoke(menuItem) ?: false
        }
    }

    /**
     * Set the navigation menu resource
     */
    fun setMenu(menuResId: Int) {
        navigationView.inflateMenu(menuResId)
    }

    /**
     * Set the navigation header resource
     */
    fun setHeaderView(headerResId: Int) {
        navigationView.inflateHeaderView(headerResId)
    }

    /**
     * Set the navigation item selected listener
     */
    fun setNavigationItemSelectedListener(listener: (MenuItem) -> Boolean) {
        navigationItemSelectedListener = listener
    }

    /**
     * Select a menu item by its ID
     */
    fun setSelectedItemId(itemId: Int) {
        // Find the menu item
        val menuItem = navigationView.menu.findItem(itemId)

        // If menu item exists, select it
        if (menuItem != null) {
            currentSelectedItemId = itemId
            updateSelectionState()
            navigationItemSelectedListener?.invoke(menuItem)
        }
    }

    /**
     * Update the selection state of menu items
     */
    private fun updateSelectionState() {
        navigationView.menu.forEach { menuItem ->
            menuItem.isChecked = menuItem.itemId == currentSelectedItemId
        }
    }

    /**
     * Get the current selected item ID
     */
    fun getSelectedItemId(): Int {
        return currentSelectedItemId
    }

    /**
     * Enable or disable a menu item
     */
    fun setMenuItemEnabled(itemId: Int, enabled: Boolean) {
        navigationView.menu.findItem(itemId)?.isEnabled = enabled
    }

    /**
     * Show or hide the navigation drawer
     */
    fun setVisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }
}