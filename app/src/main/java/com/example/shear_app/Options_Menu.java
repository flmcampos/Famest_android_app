package com.example.shear_app;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

public class Options_Menu extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
