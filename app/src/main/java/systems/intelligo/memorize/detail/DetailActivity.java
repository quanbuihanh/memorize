package systems.intelligo.memorize.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import systems.intelligo.memorize.AppMain;
import systems.intelligo.memorize.Injection;
import systems.intelligo.memorize.R;
import systems.intelligo.memorize.database.entity.Words;

public class DetailActivity extends AppCompatActivity implements DetailContract.View {

    private ImageButton favBtn;
    private LinearLayout meaningLayout;
    private LinearLayout meaningMnLayout;
    private LinearLayout tagsLayout;
    private LinearLayout partLayout;
    private TextView headKanji;
    private TextView headHiragana;
    private TextView kanji;

    private DetailContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        meaningLayout = findViewById(R.id.meanings_layout);
        meaningMnLayout = findViewById(R.id.meanings_mn_layout);
        tagsLayout = findViewById(R.id.tags_layout);
        partLayout = findViewById(R.id.part_layout);

        headKanji = (TextView) findViewById(R.id.header_kanji);
        headHiragana = (TextView) findViewById(R.id.header_hiragana);
        kanji = (TextView) findViewById(R.id.detail_kanji);

        ImageButton backBtn = findViewById(R.id.back);
        favBtn = findViewById(R.id.btnFav);
        Intent intent = getIntent();

        new DetailPresenter((Words) intent.getSerializableExtra("word_detail"),
                Injection.provideWordsRepository(getApplicationContext()),
                this);

        presenter.init();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.favoriteWord();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(AppMain.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showMissingWord() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public void setData(Words word, boolean refresh) {
        if (!refresh) {
            try {
                headKanji.setText(word.getKanji());
                headHiragana.setText(word.getCharacter());
                for (int i = 0; i < word.getMeaning().size(); i++) {
                    TextView text = new TextView(this);
                    text.setText("\u2022 " + word.getMeaning().get(i)); // <-- does it really compile without the + sign?
                    text.setTextSize(20);
                    text.setMaxLines(2);
                    text.setGravity(Gravity.START);
                    text.setLayoutParams(new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
                    meaningLayout.addView(text);
                }

                if (word.getMeaningMon().size() > 0) {
                    for (int i = 0; i < word.getMeaningMon().size(); i++) {
                        TextView text = new TextView(this);
                        text.setText("\u2022 " + word.getMeaningMon().get(i)); // <-- does it really compile without the + sign?
                        text.setTextSize(20);
                        text.setMaxLines(2);
                        text.setGravity(Gravity.START);
                        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        meaningMnLayout.addView(text);
                    }
                } else {
                    AppCompatImageButton newBtn = new AppCompatImageButton(this);
                    newBtn.setBackgroundDrawable(getDrawable(R.drawable.ic_add_box));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    meaningMnLayout.addView(newBtn, params);

                    newBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailActivity.this, R.style.DialogStyle);
                            View view = getLayoutInflater().inflate(R.layout.detail_bottom_dialog, null);
                            bottomSheetDialog.setContentView(view);

                            AppCompatButton add = view.findViewById(R.id.detail_bottom_add);
                            final AppCompatEditText text = view.findViewById(R.id.detail_add_meaning);
                            add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    presenter.addMeaning(text.getText().toString());
                                    bottomSheetDialog.cancel();
                                }
                            });

                            bottomSheetDialog.show();
                        }
                    });
                }

                assert word.getPartOfSpeech() != null;
                for (int i = 0; i < word.getPartOfSpeech().size(); i++) {
                    TextView text = new TextView(this);
                    text.setText(word.getPartOfSpeech().get(i)); // <-- does it really compile without the + sign?
                    text.setTextSize(15);
                    text.setPadding(5, 5, 5, 5);
                    text.setGravity(Gravity.START);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        text.setTextColor(getColor(R.color.white));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        text.setBackground(getDrawable(R.drawable.ic_round_rectangle));
                    }
                    text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    partLayout.addView(text);
                }

                TextView jlptText = new TextView(this);
                jlptText.setText(word.getLevel()); // <-- does it really compile without the + sign?
                jlptText.setTextSize(15);
                jlptText.setPadding(5, 5, 5, 5);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    jlptText.setTextColor(getColor(R.color.white));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    jlptText.setBackground(getDrawable(R.drawable.ic_round_rectangle));
                }
                jlptText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tagsLayout.addView(jlptText);

                assert word.getLevel() != null;
                for (int i = 0; i < word.getTag().size(); i++) {
                    TextView text = new TextView(this);
                    text.setText(word.getTag().get(i)); // <-- does it really compile without the + sign?
                    text.setTextSize(15);
                    text.setPadding(5, 5, 5, 5);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        text.setTextColor(getColor(R.color.white));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        text.setBackground(getDrawable(R.drawable.ic_round_rectangle));
                    }
                    text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    tagsLayout.addView(text);
                }

                kanji.setText(word.getKanji());
                if (word.isFavorite())
                    favBtn.setImageResource(R.drawable.ic_favorite_full);
            } catch (Exception ex) {
                showToast("Алдаа :" + ex);
            }
        } else {
            TextView textView = findViewById(R.id.meanings_mn_title);
            meaningMnLayout.removeAllViews();
            meaningMnLayout.addView(textView);
            for (int i = 0; i < word.getMeaningMon().size(); i++) {
                TextView text = new TextView(this);
                text.setText(word.getMeaningMon().get(i)); // <-- does it really compile without the + sign?
                text.setTextSize(20);
                text.setMaxLines(2);
                text.setGravity(Gravity.START);
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                meaningMnLayout.addView(text);
            }
        }
    }

    @Override
    public void showFavorite(boolean isFav) {
        if (isFav)
            favBtn.setImageResource(R.drawable.ic_favorite_full);
        else
            favBtn.setImageResource(R.drawable.ic_favorite);
    }
}
