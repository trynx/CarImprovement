package nicodo.com.myemail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends Activity {

    private CheckBox chkBox;
    private Button nextAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_const);

        chkBox = (CheckBox) findViewById(R.id.check_box);
        chkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (chkBox.isChecked()) {
                    nextAct.setVisibility(View.VISIBLE);
                } else {
                    nextAct.setVisibility(View.INVISIBLE);
                }
            }
        });
        nextAct = (Button) findViewById(R.id.next_btn);

    }




    public void nextActivity(View view) {
        Intent i = new Intent(this, SendMailActivity.class);
        finish();
        startActivity(i);
    }
}
