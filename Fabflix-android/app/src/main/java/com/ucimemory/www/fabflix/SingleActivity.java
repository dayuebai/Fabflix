package com.ucimemory.www.fabflix;

import android.os.Bundle;

public class SingleActivity extends SearchActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle bundle = getIntent().getExtras();
        String mId = bundle.get("id").toString();

        connectToTomcat(mId, "id", "1");

    }

}
