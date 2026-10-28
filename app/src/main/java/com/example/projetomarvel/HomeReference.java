//montar cards do home
package com.example.projetomarvel;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/** Home chrome from the prototype; every character card is bound to Comic Vine data. */
final class HomeReference {
    private final MainActivity host;

    HomeReference(MainActivity host) { this.host = host; }

    private int d(float value) { return host.dp(value); }
    private TextView t(String label, float size, int color) { return host.text(label, size, color); }
    private GradientDrawable bg(int start, int end, int radius, int border) {
        return host.rounded(new int[]{start, end}, radius, border, 1);
    }

    void render() {
        LinearLayout header = host.body;
        header.setPadding(d(20), d(34), d(20), d(6));
        LinearLayout page = (LinearLayout) header.getParent();

        LinearLayout titleRow = new LinearLayout(host);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        View red = new View(host);
        red.setBackground(bg(0xffe62429, 0xffe62429, 3, 0xffe62429));
        LinearLayout.LayoutParams redLp = new LinearLayout.LayoutParams(d(6), d(34));
        redLp.rightMargin = d(12);
        titleRow.addView(red, redLp);
        TextView title = t("MARVEL UNIVERSE", 34, Color.WHITE);
        title.setTypeface(Typography.bebas(host));
        title.setShadowLayer(d(8), 0, 0, 0xff9c1a1e);
        titleRow.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        header.addView(titleRow);

        TextView subtitle = t("Conheça os personagens da Marvel Comics.", 14.5f, 0xffd2c5c8);
        LinearLayout.LayoutParams subtitleLp = new LinearLayout.LayoutParams(-1, -2);
        subtitleLp.topMargin = d(5);
        header.addView(subtitle, subtitleLp);

        host.homeCount = t("Catálogo Marvel", 12.5f, 0xffaeb8d0);
        LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(-1, -2);
        countLp.topMargin = d(5);
        countLp.bottomMargin = d(14);
        header.addView(host.homeCount, countLp);

        LinearLayout story = new LinearLayout(host);
        story.setOrientation(1);
        story.setPadding(d(16), d(14), d(16), d(14));
        story.setBackground(bg(0xff102541, 0xff304e7d, 18, 0xff5aacc6));
        story.addView(t("N O V A   C A M P A N H A", 10, 0xff73e5ff));
        TextView storyTitle = t("MODO HISTÓRIA  →", 28, Color.WHITE);
        storyTitle.setTypeface(Typography.bebas(host));
        story.addView(storyTitle);
        story.addView(t("Crie seu herói. Salve seu universo.", 13, 0xffdae5ff));
        LinearLayout.LayoutParams storyLp = new LinearLayout.LayoutParams(-1, d(110));
        storyLp.bottomMargin = d(13);
        header.addView(story, storyLp);
        story.setOnClickListener(v -> host.open("História"));

        EditText search = new EditText(host);
        search.setSingleLine(true);
        search.setTextColor(Color.WHITE);
        search.setHintTextColor(0xffadb2c5);
        search.setTextSize(15);
        search.setHint("Buscar personagem...");
        search.setText(host.homeQuery);
        search.setPadding(d(15), 0, d(15), 0);
        search.setBackground(bg(0xff241c2c, 0xff1d1b29, 14, 0xff765162));
        header.addView(search, new LinearLayout.LayoutParams(-1, d(48)));
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                host.homeQuery = s.toString();
                host.refreshVisibleCatalog();
            }
            @Override public void afterTextChanged(Editable editable) {}
        });

        host.catalogStatus = t("Carregando catálogo Comic Vine…", 13, 0xffaeb8d0);
        host.catalogStatus.setPadding(d(20), d(5), d(20), d(5));
        page.addView(host.catalogStatus);
        host.heroList = new RecyclerView(host);
        host.heroList.setLayoutManager(new LinearLayoutManager(host));
        host.heroList.setClipToPadding(false);
        host.heroList.setPadding(d(20), d(4), d(20), d(100) + host.navigationInset);
        host.heroAdapter = host.new HeroAdapter();
        host.heroList.setAdapter(host.heroAdapter);
        page.addView(host.heroList, new LinearLayout.LayoutParams(-1, 0, 1));
        host.refreshVisibleCatalog();
    }
}
