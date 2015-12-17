package com.ift604.ift604_projet.projet.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ift604.ift604_projet.projet.R;

import java.util.List;

/**
 * Created by Benoit on 2015-12-14.
 */
public class RankingActivity extends CommunicationActivity {

    ListView listRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankings);

        listRanking = (ListView)findViewById(R.id.listRanking);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        List<String> l = boundService.GetRanking();
        listRanking.setAdapter(new ArrayAdapter(RankingActivity.this, android.R.layout.simple_list_item_1, l));
    }

}
