package com.batoulapps.QamarDeen;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.batoulapps.QamarDeen.data.QamarConstants;
import com.batoulapps.QamarDeen.data.QamarConstants.PreferenceKeys;

import java.util.Locale;

public class QamarPreferencesActivity extends SherlockPreferenceActivity
   implements OnPreferenceChangeListener {

   private static final int MENU_DONE = 1;
   private Preference mGenderPreference = null;
   private CheckBoxPreference mArabicPreference = null;
   private boolean mUsingArabic = false;
   private boolean mArabicChanged = false;
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      menu.add(Menu.NONE, MENU_DONE, Menu.NONE, R.string.cancel)
         .setIcon(R.drawable.ic_action_cancel)
         .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
      return super.onCreateOptionsMenu(menu);
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == MENU_DONE){
         leavePreferences();
         return true;
      }
      return super.onOptionsItemSelected(item);
   }
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      setTheme(R.style.Theme_Sherlock_Light);
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preferences);
      
      mGenderPreference = findPreference(PreferenceKeys.GENDER_PREF);
      if (mGenderPreference != null){
         mGenderPreference.setOnPreferenceChangeListener(this);
         updateGenderPreference();
      }

      PreferenceScreen about = (PreferenceScreen)findPreference("about");
      if (about != null){
         String title = getString(R.string.qamar_about_title);
         try {
            PackageInfo info = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            title = String.format(title, info.versionName);
            about.setTitle(title);
         }
         catch (Exception e){
         }
      }

      mArabicPreference = (CheckBoxPreference)findPreference(
              PreferenceKeys.USE_ARABIC);
      if (mArabicPreference != null){
         if ("ar".equals(Locale.getDefault().getLanguage())){
            mArabicPreference.setEnabled(false);
         }
         else { mArabicPreference.setOnPreferenceChangeListener(this); }
         mUsingArabic = mArabicPreference.isChecked();
      }
   }
   
   @Override
   protected void onDestroy() {
      if (mGenderPreference != null){
         mGenderPreference.setOnPreferenceChangeListener(null);
      }

      if (mArabicPreference != null){
         mArabicPreference.setOnPreferenceChangeListener(null);
      }
      super.onDestroy();
   }

   @Override
   public void onBackPressed() {
      if (mArabicChanged){
         leavePreferences();
         return;
      }
      super.onBackPressed();
   }

   private void leavePreferences(){
      if (mArabicChanged){
         Intent i = new Intent(this, QamarDeenActivity.class);
         i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(i);
      }
      finish();
   }
   
   @Override
   public boolean onPreferenceChange(Preference preference, Object newValue) {
      if (preference.getKey().equals(PreferenceKeys.GENDER_PREF)){
         updateGenderPreference(newValue);
      }
      else if (preference.getKey().equals(PreferenceKeys.USE_ARABIC)){
         if (newValue != null && newValue instanceof Boolean){
            Boolean value = (Boolean)newValue;
            if (value != mUsingArabic){
               mArabicChanged = true;
            }
            else { mArabicChanged = false; }
         }
      }
      return true;
   }
   
   private void updateGenderPreference(){
      SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(this);
      String gender = prefs.getString(
            QamarConstants.PreferenceKeys.GENDER_PREF, "");
      updateGenderPreference(gender);
   }
   
   private void updateGenderPreference(Object value){
      if (mGenderPreference != null){
         if ("female".equals(value.toString())){
            mGenderPreference.setSummary(R.string.pref_gender_female);
         }
         else {
            mGenderPreference.setSummary(R.string.pref_gender_male);
         }
      }
   }
}
