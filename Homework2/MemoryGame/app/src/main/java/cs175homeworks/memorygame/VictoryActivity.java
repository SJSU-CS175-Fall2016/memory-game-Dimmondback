package cs175homeworks.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class VictoryActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.victory_activity);

    TextView pointsText = (TextView) findViewById(R.id.victory_points);
    assert pointsText != null;
    if (this.getIntent().getExtras().get("points") != null) {
      int points = (int) this.getIntent().getExtras().get("points");
      String finalPoints = getString(R.string.victory_points) + points;
      pointsText.setText(finalPoints);
    } else {
      pointsText.setText(getString(R.string.cheated));
    }
  }

  @Override
  public void onBackPressed() {
    Intent main = new Intent(getApplicationContext(), MainActivity.class);
    main.putExtra("restart", true);
    startActivity(main);
    super.onBackPressed();
  }
}
