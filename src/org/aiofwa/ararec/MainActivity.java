package org.aiofwa.ararec;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
	ArrayList<HashMap> repo = new ArrayList<>(); 
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		final ProgressDialog pg = new ProgressDialog(this);
		pg.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
		pg.setCancelable(false); 
		pg.setMessage("Wait while we are trying to fetch repodata from the master server... "); 
		pg.setTitle("Wait.."); 
		pg.show(); 
		Thread p = new Thread() {
			@Override 
			public void run() {
				try
				{
					URL u = new URL("http://127.0.0.1:8080/www/pkgs/plist.txt");
					ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
					BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream())); 
					while (true) {
						char [] chars = new char[1024]; 
						int len = br.read(chars); 
						if (len == -1) {
							pg.dismiss(); 
							String things = baos.toString(); 
							String [] files = things.split("\n"); 
							for (String f : files) {
								HashMap h = new HashMap(); 
								String [] situ = f.split("<split>"); 
								String [] distros = situ[0].split("\\|"); 
								h.put("distro", distros); 
								h.put("dir", situ[1]); 
								h.put("name", situ[2]); 
								h.put("desc", situ[3]); 
								repo.add(h); 
							}
							// Toast.makeText(getBaseContext(), repo.toString(), Toast.LENGTH_SHORT).show(); 
							// pg.setMessage(repo.toString());
							break; 
						}
						baos.write(String.valueOf(chars).trim().getBytes()); 

						
					}
				}
				catch (Exception e)
				{
					pg.setMessage("Load FAILED! Here is the exception: " + e.toString() + "\nYou must have the internet connection in order to talk with rarec. ");
				} 
			}
		}; 
		p.start(); 
		final EditText et = (EditText) findViewById(R.id.mainEditText1); 
		et.addTextChangedListener(new TextWatcher() {

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
					String text = et.getText().toString().toLowerCase(); 
					if (text.length() == 0) {
						return; 
					}
					// Iterate thru packages 
					LinearLayout l = (LinearLayout) findViewById(R.id.mainLinearLayout1); 
					l.removeAllViews(); 
					Iterator it = repo.iterator(); 
					ArrayList<HashMap> hashes = new ArrayList<>(); 
					ArrayList<Integer> arr = new ArrayList<>(); 
					ArrayList<HashMap> hashed = new ArrayList<>(); 
					while (it.hasNext()) {
						HashMap h = (HashMap) it.next(); 
						if (((String) h.get("name")).toLowerCase().indexOf(text) != -1 || ((String) h.get("dir")).toLowerCase().indexOf(text) != -1) {
							hashes.add(h); 
							arr.add(((String) h.get("name")).toLowerCase().indexOf(text)); 
						}
					}
					Integer [] id = arr.toArray(new Integer[1]); 
					
					while (hashes.size() != 0) {
						int big = 10000; 
						int sht = 0; 
int loc = 0; 			Iterator s = arr.iterator(); 
						while (s.hasNext()) {
							int uh = (int) s.next();
							if (uh < big) {
								big = uh; 
								loc = sht; 
							}
							sht ++; 
						}
						arr.remove(loc); 
						hashed.add(hashes.get(loc)); 
						hashes.remove(loc); 
					}
					Iterator is = hashed.iterator(); 
					while (is.hasNext()) {
						final HashMap h = (HashMap) is.next(); 
							String [] distros = (String[]) h.get("distro");
							LinearLayout ll = new LinearLayout(MainActivity.this); 
							ll.setOrientation(ll.HORIZONTAL); 
							final LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
							final LayoutParams lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
							LayoutParams lp3 = new LayoutParams(50, 50); 
							ll.setLayoutParams(lp1); 
							ll.setGravity(Gravity.CENTER);
							ArrayList<ImageView> imgs = new ArrayList<>(); 
							boolean and = false; 
							for (String uh : distros) {
								if (uh.equals("LINUX")) {
									ImageView iv = new ImageView(MainActivity.this); 
									iv.setLayoutParams(lp3); 
									iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.linux));
									imgs.add(iv); 
								}
								if (uh.equals("WINDOWS")) {
									ImageView iv = new ImageView(MainActivity.this); 
									iv.setLayoutParams(lp3); 
									iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.windows));
									imgs.add(iv); 
								}
								if (uh.equals("ANDROID")) {
									ImageView iv = new ImageView(MainActivity.this); 
									iv.setLayoutParams(lp3); 
									iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.android));
									imgs.add(iv); 
									and = true; 
								}
							}
						ll.setBackgroundDrawable(new ColorDrawable(Color.rgb(55, 55, 55)));
						
							if (!and) {
								// TextView warn = new TextView(MainActivity.this); 
								// warn.setLayoutParams(lp1);
								// warn.setText("WARNING: This product may not fit to your phone. "); 
								// warn.setTypeface(Typeface.MONOSPACE, Typeface.BOLD); 
								// warn.setGravity(Gravity.CENTER); 
								// warn.setBackground(new ColorDrawable(Color.rgb(255, 199, 20))); 
								// l.addView(warn); 
								ll.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
							}
							ImageView [] ivs = imgs.toArray(new ImageView[1]); 
							if (ivs != null) {
								for (ImageView i : ivs) {
									if (i == null) {break;}
									ll.addView(i); 
								}
							}
							TextView tv = new TextView(MainActivity.this); 
							tv.setLayoutParams(lp2); 
							tv.setText("  " + (String) h.get("name")); 
							final TextView tv1 = new TextView(MainActivity.this); 
							tv1.setLayoutParams(lp2);
							tv1.setTextColor(Color.GRAY); 
							if (!and) {
								tv1.setTextColor(Color.WHITE); 
							}
							if (((String) h.get("desc")).length() <= 20) {
								tv1.setText("    " + ((String) h.get("desc"))); 
							} else {
								tv1.setText("    " + ((String) h.get("desc")).substring(0, 20) + "..."); 

								tv1.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View p1)
										{
											// TODO: Implement this method
											String n = ((String) h.get("desc")); 
											for (int a = 20; a != n.length(); a += 20) {
												if (a > n.length()) {break; }
												n = n.substring(0, a) + "\n    " + n.substring(a); 
											}
											tv1.setText("    " + n); 
											return; 
										}


									}); 
							}
						tv1.setOnLongClickListener(new View.OnLongClickListener() {

								@Override
								public boolean onLongClick(View p1)
								{
									// TODO: Implement this method
									// LinearLayout app = (LinearLayout) findViewById(R.id.appLinearLayout1);
									LinearLayout app = new LinearLayout(MainActivity.this); 
									app.setOrientation(app.VERTICAL); 
									app.setLayoutParams(lp2); 
									TextView name = new TextView(MainActivity.this); 
									name.setLayoutParams(lp1);
									name.setText(h.get("name") + " -- " + h.get("dir")); 
									ImageView avatar = new ImageView(MainActivity.this); 
									avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher)); 
									avatar.setLayoutParams(lp2); 
									View v = new View(MainActivity.this); 
									v.setLayoutParams(new LayoutParams(1, LayoutParams.MATCH_PARENT)); 
									v.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
									// app.setBackground(new ColorDrawable(Color.WHITE));
									TextView tv1 = new TextView(MainActivity.this);
									tv1.setLayoutParams(lp2); 
									tv1.setText((String) h.get("desc")); 
									LinearLayout hori = new LinearLayout(MainActivity.this); 
									app.addView(name); 
									hori.setOrientation(hori.HORIZONTAL); 
									hori.addView(avatar); 
									hori.setLayoutParams(lp2); 
									hori.addView(v); 
									hori.addView(tv1); 
									app.addView(hori); 
									AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this)
										.setTitle((String) h.get("name"))
										.setView(app)
										.setCancelable(false);
									ab.show(); 
									return true;
								}
								
								
							}); 
													tv1.setTextSize(11.0F); 
							tv1.setTypeface(Typeface.SERIF); 
							ll.addView(tv); 
							ll.addView(tv1); 
							l.addView(ll); 
							View v = new View(getBaseContext()); 
							LayoutParams sep = new LayoutParams(LayoutParams.MATCH_PARENT, 1); 
							v.setLayoutParams(sep); 
							v.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
							l.addView(v); 
						}
					}
				
			
		}); 
    }
}
