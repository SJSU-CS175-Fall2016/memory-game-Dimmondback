package cs175homeworks.memorygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
  private HashMap<String, Integer> saveData;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    saveData = new HashMap<>();
    if (savedInstanceState != null || this.getIntent().getExtras() != null) {
      Bundle bundle;
      if (savedInstanceState != null) {
        bundle = savedInstanceState;
      } else {
        bundle = this.getIntent().getExtras();
      }
      saveData.put("height", (int) bundle.get("height"));
      saveData.put("width", (int) bundle.get("width"));
      saveData.put("checked", (int) bundle.get("checked"));
      saveData.put("points", (int) bundle.get("points"));
      saveData.put("flipped", (int) bundle.get("flipped"));
      for (int i = 0; i < saveData.get("height"); i++) {
        for (int j = 0; j < saveData.get("width"); j++) {
          String key = "" + i + j;
          saveData.put(key, (int) bundle.get(key));
        }
      }
    }

    Button playButton = (Button) findViewById(R.id.play_button);
    if (playButton != null) {
      playButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent game = new Intent(getApplicationContext(), GameActivity.class);
          if (saveData.get("height") != null) {
            game.putExtra("points", saveData.get("points"));
            game.putExtra("checked", saveData.get("checked"));
            for (int i = 0; i < saveData.get("height"); i++) {
              for (int j = 0; j < saveData.get("width"); j++) {
                String key = "" + i + j;
                game.putExtra(key, (int) saveData.get(key));
              }
            }
            System.out.println("mainPoints Click: " + (saveData.get("points")));
          }
          startActivity(game);
          finish();
        }
      });
    }

    Button rulesButton = (Button) findViewById(R.id.rules_button);
    if (rulesButton != null) {
      rulesButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent game = new Intent(getApplicationContext(), RulesActivity.class);
          startActivity(game);
        }
      });
    }
  }
}
