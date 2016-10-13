package cn.ben.googletrainingsavingdata.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.ben.googletrainingsavingdata.R;

public class SavingFilesFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = SavingFilesFragment.class.getSimpleName();
    private Context mContext;
    private EditText inputFileName;
    private TextView textView;
    private ArrayList<File> fileArrayList;
    private final String internalFileName = "my_file";
    private Button btnCreateInternalConstructor, btnWriteInternalStream, btnGetTempFile, btnCheckExternalState, btnGetAlbumStorageDir, btnDeleteFiles, btnReset;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        fileArrayList = new ArrayList<>();
    }

    public SavingFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saving_files, container, false);

        inputFileName = (EditText) view.findViewById(R.id.input_file_name);
        textView = (TextView) view.findViewById(R.id.tv);
        btnCreateInternalConstructor = (Button) view.findViewById(R.id.btn_create_internal_constructor);
        btnCreateInternalConstructor.setOnClickListener(this);
        btnWriteInternalStream = (Button) view.findViewById(R.id.btn_write_internal_stream);
        btnWriteInternalStream.setOnClickListener(this);
        btnGetTempFile = (Button) view.findViewById(R.id.btn_get_temp_file);
        btnGetTempFile.setOnClickListener(this);
        btnCheckExternalState = (Button) view.findViewById(R.id.btn_check_external_state);
        btnCheckExternalState.setOnClickListener(this);
        btnGetAlbumStorageDir = (Button) view.findViewById(R.id.btn_get_album_storage_dir);
        btnGetAlbumStorageDir.setOnClickListener(this);
        btnDeleteFiles = (Button) view.findViewById(R.id.btn_delete_file);
        btnDeleteFiles.setOnClickListener(this);
        btnReset = (Button) view.findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);

        return view;
    }

    private File createFileInternalUsingFileConstructor(String filename) {
        return new File(mContext.getFilesDir(), filename);
    }

    private void writeFileInternalUsingOutputStream() {
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput(internalFileName, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
            fileArrayList.add(file);
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        fileArrayList.add(file);
        return file;
    }

    private File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        fileArrayList.add(file);
        return file;
    }

    private boolean deleteFile(File file) {
        return file.delete();
    }

    private boolean deleteInternalFile(Context myContext, String fileName) {
        return myContext.deleteFile(fileName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_internal_constructor:
                if (inputFileName.getText().toString().equals("")) {
                    textView.setText(R.string.input_hint);
                    return;
                }
                File file = createFileInternalUsingFileConstructor(inputFileName.getText().toString());
                fileArrayList.add(file);
                textView.setText(String.format("%s%s", getString(R.string.create_file_success), file.getAbsolutePath()));
                v.setVisibility(View.GONE);
                break;
            case R.id.btn_write_internal_stream:
                writeFileInternalUsingOutputStream();
                textView.setText(R.string.finish_work);
                v.setVisibility(View.GONE);
                break;
            case R.id.btn_get_temp_file:
                String url = "https://tapd.tencent.com/Heroes_Moba_International/markdown_wikis";
                file = getTempFile(mContext, url);
                textView.setText(String.format("%s%s", getString(R.string.create_file_success), file.getAbsolutePath()));
                v.setVisibility(View.GONE);
                break;
            case R.id.btn_check_external_state:
                textView.setText("isExternalStorageWritable? " + isExternalStorageWritable() + "\n" + "isExternalStorageReadable? " + isExternalStorageReadable());
                v.setVisibility(View.GONE);
                break;
            case R.id.btn_get_album_storage_dir:
                String albumName = "ben_yang";
                textView.setText("public: " + getPublicAlbumStorageDir(albumName) + "\nprivate: " + getPrivateAlbumStorageDir(mContext, albumName));
                v.setVisibility(View.GONE);
                break;
            case R.id.btn_delete_file:
                StringBuilder sb = new StringBuilder();
                for (File file1 : fileArrayList) {
                    boolean res = deleteFile(file1);
                    boolean exist = file1.exists();
                    sb.append(file1.getAbsoluteFile()).append(": ").append(res).append(" ").append(exist).append("\n");
                }
                fileArrayList.clear();
                boolean res = deleteInternalFile(mContext, internalFileName);
                sb.append("Internal file: ").append(res).append("\n");
                textView.setText(sb.toString());
                v.setVisibility(View.GONE);
                break;
            case R.id.btn_reset:
                for (File file1 : fileArrayList) {
                    deleteFile(file1);
                }
                deleteInternalFile(mContext, internalFileName);
                v.setVisibility(View.GONE);
                textView.setText("");
                btnCheckExternalState.setVisibility(View.VISIBLE);
                btnCreateInternalConstructor.setVisibility(View.VISIBLE);
                btnDeleteFiles.setVisibility(View.VISIBLE);
                btnGetAlbumStorageDir.setVisibility(View.VISIBLE);
                btnGetTempFile.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                btnWriteInternalStream.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
