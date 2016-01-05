package me.isassist.isa;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Hashtable;

public class DetailActivity extends AppCompatActivity {

    private HashMap<String, String> mData;
    private Location mLocation;
    private Bihapi mAPIType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Button navigateButton = (Button) findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = mData.get("lat");
                String lon = mData.get("lon");
                /*
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);*/
                String uri = "http://maps.google.com/maps?daddr="+lat+","+lon;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("DATA")) {
            mData = (HashMap<String, String>) intent.getSerializableExtra("DATA");
            mLocation = new Location("");
            mLocation.setLongitude(intent.getDoubleExtra("LON", 0));
            mLocation.setLatitude(intent.getDoubleExtra("LAT", 0));
            mAPIType = (Bihapi) intent.getSerializableExtra("API_TYPE");
        }
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        TextView description = (TextView) findViewById(R.id.objectName);

        switch (mAPIType) {
            case CITY_OFFICES:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                addRow(tableLayout, "Email: ", mData.get("MAIL"), ValueType.EMAIL);
                break;
            case CASH_MACHINES:
                if (mData.get("WWW_BANKU").equals("http://www.euronetpolska.pl"))
                    description.setText("Euronet");
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Location:", mData.get("LOKALIZACJA"), ValueType.TEXT);
                addRow(tableLayout, "Access:", mData.get("DOSTEP"), ValueType.TEXT);
                addRow(tableLayout, "WWW:", mData.get("WWW_BANKU"), ValueType.WWW);
                break;
            case DORMITORIES:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                break;
            case PHARMACIES:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Opening hours:", mData.get("godziny_pracy"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                addRow(tableLayout, "Email: ", mData.get("MAIL"), ValueType.EMAIL);
                break;
            case HOTELS:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Standard:", mData.get("GWIAZDKI"), ValueType.TEXT);
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Capacity:", mData.get("POJEMNOSC"), ValueType.TEXT);
                addRow(tableLayout, "Rooms:", mData.get("POKOJE"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                addRow(tableLayout, "WWW:", mData.get("WWW"), ValueType.WWW);
                addRow(tableLayout, "Email: ", mData.get("MAIL"), ValueType.EMAIL);
                break;
            case POLICE_OFFICES:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                addRow(tableLayout, "WWW:", mData.get("WWW"), ValueType.WWW);
                addRow(tableLayout, "Email: ", mData.get("MAIL"), ValueType.EMAIL);
                break;
            case SPORT_FIELDS:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Address:", mData.get("ULICA"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                addRow(tableLayout, "WWW:", mData.get("WWW"), ValueType.WWW);
                addRow(tableLayout, "Email: ", mData.get("MAIL1"), ValueType.EMAIL);
                break;
            case SWIMMING_POOLS:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                addRow(tableLayout, "WWW:", mData.get("WWW"), ValueType.WWW);
                addRow(tableLayout, "Email: ", mData.get("MAIL1"), ValueType.EMAIL);
                break;
            case VETURILO:
                description.setText(mData.get("LOKALIZACJA"));
                addRow(tableLayout, "Station ID:", mData.get("NR_STACJI"), ValueType.TEXT);
                addRow(tableLayout, "Bicycles:", mData.get("ROWERY"), ValueType.TEXT);
                addRow(tableLayout, "Racks:", mData.get("STOJAKI"), ValueType.TEXT);
                break;
            case THEATRES:
                description.setText(Html.fromHtml(mData.get("OPIS")));
                addRow(tableLayout, "Address:", mData.get("ULICA") + " " + mData.get("NUMER"), ValueType.TEXT);
                addRow(tableLayout, "District:", mData.get("DZIELNICA"), ValueType.TEXT);
                addRow(tableLayout, "Tel:", mData.get("TEL_FAX"), ValueType.TEXT);
                addRow(tableLayout, "WWW:", mData.get("WWW"), ValueType.WWW);
                break;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addRow(TableLayout layout, String name, String value, ValueType type)
    {
        if (value == null || value.equals(""))
            return;

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TableRow row = new TableRow(this);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setLayoutParams(params);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvName.setPadding(0, 0, 10, 0);

        TextView tvValue = new TextView(this);
        if (type == ValueType.WWW)
        {
            tvValue.setText(Html.fromHtml("<a href=" + value + ">" + value + "</a> "));
            tvValue.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else if (type == ValueType.EMAIL)
        {
            if (mAPIType == Bihapi.PHARMACIES)
            {
                tvValue.setText(Html.fromHtml("<a href=" + "mailto:" + value + ">" + value + "</a> "));
            }
            else {
                tvValue.setText(Html.fromHtml("<a href=" + value + ">" + value.replace("mailto:", "") + "</a> "));
            }
            tvValue.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            tvValue.setText(value);
        }
        tvValue.setLayoutParams(params);
        tvValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        row.addView(tvName);
        row.addView(tvValue);
        row.setPadding(10, 10, 10, 10);
        layout.addView(row);
    }

    private enum ValueType
    {
        WWW, EMAIL, TEXT
    }
}
