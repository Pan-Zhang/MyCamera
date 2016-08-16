package lucky.main.mycamera;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import lucky.main.mycamera.camera.CameraActivity;
import lucky.main.mycamera.camera.Common;

public class MainActivity extends AppCompatActivity {
    private final static int MY_RESULT = 999;
    public Button take_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Common.getSDCardPath()+"/ZPCamera/test.jpg");
        startActivityForResult(intent, MY_RESULT);
        take_photo = (Button) findViewById(R.id.take_photo);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Common.getSDCardPath()+"/ZPCamera/test.jpg");
                startActivityForResult(intent, MY_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_RESULT && resultCode==-1){
            Uri path = data.getData();
            Toast.makeText(this, path.getPath(), Toast.LENGTH_LONG).show();
        }
    }
}
