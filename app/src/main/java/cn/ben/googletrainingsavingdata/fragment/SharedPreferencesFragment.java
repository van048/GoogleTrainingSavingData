package cn.ben.googletrainingsavingdata.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.ben.googletrainingsavingdata.R;

public class SharedPreferencesFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences sp1;
    @SuppressWarnings("unused")
    SharedPreferences sp2;
    private EditText inputHighScore;

    public SharedPreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sp1 = getHandleToSP1();
        sp2 = getHandleToSP2();
    }

    private void writeNewHighScoreToSP1(int newHighScore) {
        if (sp1 == null) sp1 = getHandleToSP1();
        SharedPreferences.Editor editor = sp1.edit();
        editor.putInt(getString(R.string.saved_high_score), newHighScore);
        editor.apply();
    }

    private long readHighScoreFromSP1() {
        if (sp1 == null) sp1 = getHandleToSP1();
        int defaultValue = getResources().getInteger(R.integer.saved_high_score_default);
        return (long) sp1.getInt(getString(R.string.saved_high_score), defaultValue);
    }

    private SharedPreferences getHandleToSP2() {
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    private SharedPreferences getHandleToSP1() {
        Context context = getActivity();
        return context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shared_preferences, container, false);

        Button btnWritePref = (Button) view.findViewById(R.id.btn_write_pref);
        btnWritePref.setOnClickListener(this);
        Button btnReadPref = (Button) view.findViewById(R.id.btn_read_pref);
        btnReadPref.setOnClickListener(this);
        inputHighScore = (EditText) view.findViewById(R.id.input_high_score);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_write_pref:
                String scoreStr = inputHighScore.getText().toString();
                int score;
                try {
                    score = Integer.parseInt(scoreStr);
                    writeNewHighScoreToSP1(score);
                    Toast.makeText(getContext(), "Modification Applying!", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Wrong input!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_read_pref:
                Toast.makeText(getContext(), "current high score: " + readHighScoreFromSP1(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
