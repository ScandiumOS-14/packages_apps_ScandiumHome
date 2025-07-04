/*
 * Copyright (C) 2021 AospExtended ROM Project
 *               2023 Evolution X
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package com.scandium.home.fragments;

 import android.content.ContentResolver;
 import android.content.Context;
 import android.content.res.Resources;
 import android.content.pm.PackageManager;
 import android.graphics.drawable.AnimationDrawable;
 import android.graphics.drawable.Drawable;
 import android.os.Bundle;
 import android.provider.SearchIndexableResource;
 import android.provider.Settings;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.Gravity;
 import android.view.ViewGroup;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.FrameLayout;
 import android.widget.TextView;
 import android.text.TextUtils;
 import android.view.ViewGroup.LayoutParams;
 
 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.preference.Preference;
 import androidx.preference.Preference.OnPreferenceChangeListener;
 import androidx.preference.PreferenceScreen;
 import androidx.preference.PreferenceViewHolder;
 import androidx.recyclerview.widget.GridLayoutManager;
 import androidx.recyclerview.widget.RecyclerView.ViewHolder;
 import androidx.recyclerview.widget.RecyclerView;
 
 import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
 import com.android.settings.R;
 import com.android.settings.search.BaseSearchIndexProvider;
 import com.android.settingslib.search.Indexable;
 import com.android.settings.SettingsPreferenceFragment;
 
 import com.bumptech.glide.Glide;
 
 import com.android.internal.util.scandium.ThemeUtils;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Arrays;
 
 import org.json.JSONObject;
 import org.json.JSONException;
 
 public class DataStyles extends SettingsPreferenceFragment {
 
     private RecyclerView mRecyclerView;
     private ThemeUtils mThemeUtils;
     private String mCategory = "android.theme.customization.sb_data";
 
     private List<String> mPkgs;
 
     @Override
     public void onCreate(Bundle icicle) {
         super.onCreate(icicle);
         getActivity().setTitle(R.string.theme_customization_data_title);
 
         mThemeUtils = new ThemeUtils(getActivity());
         mPkgs = mThemeUtils.getOverlayPackagesForCategory(mCategory, "com.android.systemui");
     }
 
     @Override
     public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
             @Nullable Bundle icicle) {
         View view = inflater.inflate(
                 R.layout.item_view, container, false);
 
         mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
         GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
         mRecyclerView.setLayoutManager(gridLayoutManager);
         Adapter mAdapter = new Adapter(getActivity());
         mRecyclerView.setAdapter(mAdapter);
 
         return view;
     }
 
     @Override
     public int getMetricsCategory() {
         return MetricsEvent.SCANDIUM_HOME;
     }
 
     @Override
     public void onResume() {
         super.onResume();
     }
 
     public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {
         Context context;
         String mSelectedPkg;
         String mAppliedPkg;
 
         public Adapter(Context context) {
             this.context = context;
         }
 
         @Override
         public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
             View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_option, parent, false);
             CustomViewHolder vh = new CustomViewHolder(v);
             return vh;
         }
 
         @Override
         public void onBindViewHolder(CustomViewHolder holder, final int position) {
             String dataPkg = mPkgs.get(position);
 
             holder.image1.setBackgroundDrawable(getDrawable(holder.image1.getContext(), dataPkg, "ic_4g_mobiledata"));
             holder.image2.setBackgroundDrawable(getDrawable(holder.image2.getContext(), dataPkg, "ic_lte_mobiledata"));
             holder.image3.setBackgroundDrawable(getDrawable(holder.image3.getContext(), dataPkg, "ic_5g_mobiledata"));
 
             String currentPackageName = mThemeUtils.getOverlayInfos(mCategory, "com.android.systemui").stream()
                 .filter(info -> info.isEnabled())
                 .map(info -> info.packageName)
                 .findFirst()
                 .orElse("com.android.systemui");
 
             holder.name.setText("com.android.systemui".equals(dataPkg) ? "Default" : getLabel(holder.name.getContext(), dataPkg));
 
             if (currentPackageName.equals(dataPkg)) {
                 mAppliedPkg = dataPkg;
                 if (mSelectedPkg == null) {
                     mSelectedPkg = dataPkg;
                 }
             }
 
             holder.itemView.setActivated(dataPkg == mSelectedPkg);
             holder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     updateActivatedStatus(mSelectedPkg, false);
                     updateActivatedStatus(dataPkg, true);
                     mSelectedPkg = dataPkg;
                     enableOverlays(position);
                 }
             });
         }
 
         @Override
         public int getItemCount() {
             return mPkgs.size();
         }
 
         public class CustomViewHolder extends RecyclerView.ViewHolder {
             TextView name;
             ImageView image1;
             ImageView image2;
             ImageView image3;
             public CustomViewHolder(View itemView) {
                 super(itemView);
                 name = (TextView) itemView.findViewById(R.id.option_label);
                 image1 = (ImageView) itemView.findViewById(R.id.image1);
                 image2 = (ImageView) itemView.findViewById(R.id.image2);
                 image3 = (ImageView) itemView.findViewById(R.id.image3);
             }
         }
 
         private void updateActivatedStatus(String pkg, boolean isActivated) {
             int index = mPkgs.indexOf(pkg);
             if (index < 0) {
                 return;
             }
             RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(index);
             if (holder != null && holder.itemView != null) {
                 holder.itemView.setActivated(isActivated);
             }
         }
     }
 
     public Drawable getDrawable(Context context, String pkg, String drawableName) {
         if (pkg.equals("com.android.systemui"))
             pkg = "com.android.settings";
         try {
             PackageManager pm = context.getPackageManager();
             Resources res = pm.getResourcesForApplication(pkg);
             int resId = res.getIdentifier(drawableName, "drawable", pkg);
             return res.getDrawable(resId);
         }
         catch (PackageManager.NameNotFoundException e) {
             e.printStackTrace();
         }
         return null;
     }
 
     public String getLabel(Context context, String pkg) {
         PackageManager pm = context.getPackageManager();
         try {
             return pm.getApplicationInfo(pkg, 0)
                     .loadLabel(pm).toString();
         } catch (PackageManager.NameNotFoundException e) {
             e.printStackTrace();
         }
         return pkg;
     }
 
     public void enableOverlays(int position) {
         mThemeUtils.setOverlayEnabled(mCategory, mPkgs.get(position), "com.android.systemui");
     }
 }