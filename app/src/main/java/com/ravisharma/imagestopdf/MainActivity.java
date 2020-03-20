package com.ravisharma.imagestopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int GALLERY_PICTURE = 1;
    Button btn_select, btn_convert;
    ViewPager vp_image;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    String filePath;
    public static final int REQUEST_PERMISSIONS = 1;

    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        listener();
        fn_permission();
    }

    public void init() {
        btn_convert = findViewById(R.id.btn_convert);
        btn_select = findViewById(R.id.btn_select);
        vp_image = findViewById(R.id.vp_image);
    }

    private void listener() {
        btn_convert.setOnClickListener(this);
        btn_select.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select:
                /*Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, GALLERY_PICTURE);*/

                TedImagePicker.with(this)
                        .startMultiImage(new OnMultiSelectedListener() {
                            @Override
                            public void onSelected(@NonNull List<? extends Uri> uriList) {
                                imagesEncodedList = new ArrayList<>();
                                for (Uri uri : uriList) {
                                    try {
                                        String filePath = PathUtil.getPath(MainActivity.this, uri);
                                        imagesEncodedList.add(filePath);

                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }

                                ViewAdapter adapter = new ViewAdapter();
                                vp_image.setAdapter(adapter);
                            }
                        });

                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);*/

                break;

            case R.id.btn_convert:
                try {
                    createPdf();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void createPdf() throws IOException, DocumentException {

        Document document = new Document();

        String directoryPath = Environment.getExternalStorageDirectory().toString();

        PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/test.pdf")); //  Change pdf's name.

        document.open();

        for (String filePath : imagesEncodedList) {

            Image image = Image.getInstance(filePath);  // Change image's name and extension.
            image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            image.setAlignment(Image.ALIGN_CENTER);
            document.add(image);
        }

        document.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            imagesEncodedList = new ArrayList<String>();
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded = cursor.getString(columnIndex);

                imagesEncodedList.add(imageEncoded);
                cursor.close();
                ViewAdapter adapter = new ViewAdapter();
                vp_image.setAdapter(adapter);

                cursor.close();

                btn_convert.setClickable(true);
            }
        }*/
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    class ViewAdapter extends PagerAdapter {

        LayoutInflater layoutInflater;

        public ViewAdapter() {

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.adapter_layout, container, false);
            container.addView(view);

            ImageView imageView = view.findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(imagesEncodedList.get(position));

//            Log.d("LOGTAG", String.valueOf(imagesEncodedList.get(position)));

            imageView.setImageBitmap(bitmap);

            return view;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return imagesEncodedList.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }

}
