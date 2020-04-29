package it.lorenzopantano.texttospeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ChangeVoiceActivity extends AppCompatActivity {

    private RecyclerView voiceRecycler;
    private RecyclerView.Adapter voiceAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Voice> voices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_voice);
        setTitle("Choose a voice");

        //Prendi la lista delle voci disponibili per la lingua attualmente selezionata
        Bundle extras = getIntent().getExtras();
        voices = (ArrayList<Voice>) extras.get("avVoices");


        //Recycler View Setup
        voiceRecycler = findViewById(R.id.rvVoices);
        layoutManager = new LinearLayoutManager(this);
        voiceRecycler.setLayoutManager(layoutManager);
        voiceAdapter = new VoiceAdapter(voices);
        voiceRecycler.setAdapter(voiceAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent selectedVoice = new Intent();
        selectedVoice.putExtra("selectedVoice", -1);
        setResult(RESULT_OK, selectedVoice);
        finish();
    }


    //Adapter per la recyclerView
    public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.ViewHolder> implements View.OnClickListener {

        private ArrayList<Voice> voices;

        public VoiceAdapter(ArrayList<Voice> avVoices) {
            voices = avVoices;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_recyclerview, parent, false);
            ChangeVoiceActivity.VoiceAdapter.ViewHolder viewHolder = new ChangeVoiceActivity.VoiceAdapter.ViewHolder(constraintLayout);
            constraintLayout.setOnClickListener(this);
            return viewHolder;
        }

        @Override
        public void onClick(View v) {
            //TODO: implementa il cambio voce
            Intent selectedVoice = new Intent();
            int itemPosition = voiceRecycler.getChildLayoutPosition(v);
            selectedVoice.putExtra("selectedVoice", itemPosition);
            setResult(101, selectedVoice);
            finish();
        }

        @Override
        public void onBindViewHolder(@NonNull ChangeVoiceActivity.VoiceAdapter.ViewHolder holder, int position) {
            holder.tvVoiceRecy.setText(voices.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return voices.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvVoiceRecy;

            public ViewHolder(@NonNull ConstraintLayout constraintLayout) {
                super(constraintLayout);
                tvVoiceRecy = constraintLayout.findViewById(R.id.tvVoiceRecy);
            }

        }
    }
}
