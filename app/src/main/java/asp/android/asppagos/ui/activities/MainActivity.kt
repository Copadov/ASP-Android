package asp.android.asppagos.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import asp.android.aspandroidcore.utils.Encrypted
import asp.android.asppagos.R
import asp.android.asppagos.databinding.ActivityMainBinding
import asp.android.asppagos.utils.PROPERTY_REGISTER_SUCCESS
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.hideKeyboard

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var encripted: Encrypted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        encripted = Encrypted

        this.hideKeyboard()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        intent.let {
            if (it.getStringExtra("SELECTION") == "LOGIN") {
                navGraph.setStartDestination(R.id.registerLogin)
            } else {
                navGraph.setStartDestination(R.id.registerRequestFragment)
            }
        }

        if (Prefs.get(PROPERTY_REGISTER_SUCCESS)) {
            navGraph.setStartDestination(R.id.registerInfo)
        }

        navController.graph = navGraph

        appBarConfiguration = AppBarConfiguration(navController.graph)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}