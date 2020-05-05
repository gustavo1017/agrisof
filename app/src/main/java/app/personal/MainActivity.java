package app.personal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    String dniSupervisor = "", current_idCosto = "", current_idIP = "";
    DataBaseHelper myDatabase;
    ArrayList<ObjectRecord> records = new ArrayList<>();
    Spinner spWorker, spWorkerZone, spActivity, spCondicion, spClon, spEstadoFisico, spEstadoSanitario, spEstadoSitio;
    EditText txtHora, txtAvance, txtQr;
    //MIOS
    EditText txtDT,txtNroLinea, txtNroArbol, txtEdad, txtObservacion;
    EditText dtpFecha;

    Calendar myCalendar = Calendar.getInstance();

    TableLayout table;
    String id_personal, fecha, personal, id_zonaTrabajo, zonatrabajo, id_actividad, actividad,id_clon, decripcion_clon;
    String id_condicion, descripcion_condicion, id_estadofisico, descripcion_estadofisico, id_estadosanitario, descripcion_estadosanitario,qr = "";
    String id_estadositio, descripcion_estadositio;
    ObjectAgro cuadrilla = new ObjectAgro();
    ArrayList<ObjectAgro> workers = new ArrayList<ObjectAgro>();
    ArrayList<ObjectAgro> listCondicion = new ArrayList<ObjectAgro>();
    ArrayList<ObjectAgro> listClon = new ArrayList<ObjectAgro>();
    ArrayList<ObjectAgro> listEstadoFisico = new ArrayList<ObjectAgro>();
    ArrayList<ObjectAgro> listEstadoSanitario = new ArrayList<ObjectAgro>();
    ArrayList<ObjectAgro> listEstadoSitio = new ArrayList<ObjectAgro>();
    ArrayList<ObjectAgro> workerZone = new ArrayList<ObjectAgro>();
    ArrayList<ObjectAgro> activities = new ArrayList<ObjectAgro>();


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean checkPermission = (permission == PackageManager.PERMISSION_GRANTED);

//        Toast toastPermission = Toast.makeText(this,
//                "Is permission granted? " + checkPermission,
//                Toast.LENGTH_SHORT);
//
//        LinearLayout toastLayoutPermission = (LinearLayout) toastPermission.getView();
//        TextView toastTVPermission = (TextView) toastLayoutPermission.getChildAt(0);
//        toastTVPermission.setTextSize(30);
//        toastPermission.show();

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabase = new DataBaseHelper(MainActivity.this);

        Bundle bundle = getIntent().getExtras();
        dniSupervisor = bundle.getString("dniSupervisor");
        getFromDB();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        fecha = formatter.format(new Date());

        //Mio
        txtDT = (EditText) findViewById(R.id.txtDt);
        txtNroLinea = (EditText) findViewById(R.id.txtNroLinea);
        txtNroArbol = (EditText) findViewById(R.id.txtNroArbol);
        txtEdad = (EditText) findViewById(R.id.txtEdad);
        txtObservacion = (EditText) findViewById(R.id.txtObservacion);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                String myFormat = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                fecha = sdf.format(myCalendar.getTime());
                dtpFecha.setText(fecha);
            }
        };

        dtpFecha = findViewById(R.id.dtpFecha);
        if (dtpFecha != null) {
            dtpFecha.setFocusable(false);
            dtpFecha.setClickable(true);
            dtpFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(MainActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        }

        spWorker = findViewById(R.id.spWorker);
        spWorker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (workers.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_personal = workers.get(pos).id;
                        personal = workers.get(pos).descripcion;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spCondicion = findViewById(R.id.spCondicion);
        spCondicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (listCondicion.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_condicion = listCondicion.get(pos).id;
                        descripcion_condicion = listCondicion.get(pos).descripcion;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spClon = findViewById(R.id.spClon);
        spClon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (listClon.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_clon = listClon.get(pos).id;
                        decripcion_clon = listClon.get(pos).descripcion;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spEstadoFisico = findViewById(R.id.spEstadoFisico);
        spEstadoFisico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (listEstadoFisico.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_estadofisico = listEstadoFisico.get(pos).id;
                        descripcion_estadofisico = listEstadoFisico.get(pos).descripcion;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spEstadoSanitario= findViewById(R.id.spEstadoSanitario);
        spEstadoSanitario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (listEstadoSanitario.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_estadosanitario = listEstadoSanitario.get(pos).id;
                        descripcion_estadosanitario = listEstadoSanitario.get(pos).descripcion;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spEstadoSitio= findViewById(R.id.spEstadoSitio);
        spEstadoSitio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (listEstadoSitio.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_estadositio = listEstadoSitio.get(pos).id;
                        descripcion_estadositio = listEstadoSitio.get(pos).descripcion;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spWorkerZone = findViewById(R.id.spWorkerZone);
        spWorkerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (workerZone.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_zonaTrabajo = workerZone.get(pos).id;
                        zonatrabajo = workerZone.get(pos).descripcion;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spActivity = findViewById(R.id.spActivity);
        spActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if ((i == 0)) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#717171"));
                }

                if (activities.size() > 0) {
                    if (!(i == 0)) {
                        int pos = i - 1;
                        id_actividad = activities.get(pos).id;
                        actividad = activities.get(pos).descripcion;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        txtHora = findViewById(R.id.txtHoras);
        txtAvance = findViewById(R.id.txtAdvance);
        txtQr = findViewById(R.id.txtQR);
        txtQr.setFocusable(false);
        txtQr.setClickable(true);
        txtQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

        final Button btnSicRec = findViewById(R.id.btnSicRec);
        final Button bexportar = findViewById(R.id.bexportar);
        final Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabase.saveTablePermanent(dniSupervisor);
                MostrarDialogo("Datos Guardados permanentemente");
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtHora.getText().toString().isEmpty()) {
                    MostrarDialogo("Ingrese Hora");
                    return;
                }

                if (dtpFecha.getText().toString().isEmpty()) {
                    MostrarDialogo("Ingrese la fecha");
                    return;
                }

                if (txtAvance.getText().toString().isEmpty()) {
                    MostrarDialogo("Ingrese Avance");
                    return;
                }

                if (txtQr.getText().toString().isEmpty()) {
                    MostrarDialogo("Escanee un QR");
                    return;
                }

                if (spWorker.getSelectedItemPosition() <= 0 ||
                        spWorker.getSelectedItem() == null ||
                        spWorkerZone.getSelectedItemPosition() <= 0 ||
                        spWorkerZone.getSelectedItem() == null ||
                        spActivity.getSelectedItem() == null ||
                        spActivity.getSelectedItemPosition() <= 0) {

                    MostrarDialogo("Debe seleccionar todos las opciones");
                    return;
                }

                if (current_idCosto.length() == 0) {
                    myDatabase.insertMapeo(cuadrilla.id, fecha, id_personal,
                            id_actividad, id_zonaTrabajo,
                            txtHora.getText().toString(), txtAvance.getText().toString(),
                            txtQr.getText().toString());

                    MostrarDialogo("Se agrego la información");
                } else {
                    myDatabase.updateMapeo(current_idCosto, cuadrilla.id, fecha, id_personal,
                            id_actividad, id_zonaTrabajo,
                            txtHora.getText().toString(), txtAvance.getText().toString(),
                            txtQr.getText().toString());

                    MostrarDialogo("Se actualizo la información");

                    current_idCosto = "";
                    resetTableStyle();
                }

                if (current_idIP.length() == 0) {
                    myDatabase.insertMapeo2(fecha, txtDT.getText().toString(), id_zonaTrabajo, id_estadofisico,
                            txtNroLinea.getText().toString(), id_estadosanitario, txtNroArbol.getText().toString(), id_condicion, id_estadositio,
                            txtEdad.getText().toString(), id_clon, txtObservacion.getText().toString(),
                            txtQr.getText().toString());

                    MostrarDialogo("Se agrego la información");
                } else {
                    myDatabase.updateMapeo(current_idCosto, cuadrilla.id, fecha, id_personal,
                            id_actividad, id_zonaTrabajo,
                            txtHora.getText().toString(), txtAvance.getText().toString(),
                            txtQr.getText().toString());

                    MostrarDialogo("Se actualizo la información");

                    current_idCosto = "";
                    resetTableStyle();
                }

                RestoreDatabase();
                spWorker.setSelection(0);
                spWorkerZone.setSelection(0);
                spActivity.setSelection(0);
                txtHora.setText("");
                txtAvance.setText("");
                txtQr.setText("");
            }
        });

        table = findViewById(R.id.tabla);

        StringBuilder sbSupervisor = new StringBuilder("Supervisor: ");
        sbSupervisor.append(dniSupervisor);

        StringBuilder sbCuadrilla = new StringBuilder("Cuadrilla: ");
        sbCuadrilla.append(cuadrilla.descripcion);

        btnSicRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFromDB();

                workers = myDatabase.RestoreFromDbPersonal();
                LLenar(spWorker, 1);

                workerZone = myDatabase.RestoreFromDbZonaTrabajo();
                LLenar(spWorkerZone, 2);

                activities = myDatabase.RestoreFromDbActividades();
                LLenar(spActivity, 3);

                listCondicion = myDatabase.RestoreFromDbCondicion();
                LLenar(spCondicion, 4);

                listClon = myDatabase.RestoreFromDbClon();
                LLenar(spClon, 5);

                listEstadoFisico = myDatabase.RestoreFromDbEstadoFisico();
                LLenar(spEstadoFisico, 6);

                listEstadoSanitario = myDatabase.RestoreFromDbEstadoSanitario();
                LLenar(spEstadoFisico, 7);

                listEstadoSitio = myDatabase.RestoreFromDbEstadoSitio();
                LLenar(spEstadoSitio, 8);


                MostrarDialogo("Se importo satisfactoriamente");
            }
        });

        Button btnRestore = findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabase.deleteAllCostos();
                myDatabase.LeerInicial(MainActivity.this);
                myDatabase.readPlanificacion(MainActivity.this);
                RestoreDatabase();
                MostrarDialogo("Se restauro satisfactoriamente");
            }
        });

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_idCosto.length() == 0) {
                    MostrarDialogo("Seleccione un registro para borrar");
                } else {
                    myDatabase.deleteMapeo(current_idCosto);
                    RestoreDatabase();
                    MostrarDialogo("Se elimino el registro");
                }
            }
        });

        Button btnExport = findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions();
                // myDatabase.writeExcelFile(0, dniSupervisor);
                File txt = myDatabase.writeTXTFile(dniSupervisor);
                if (txt != null) {
                    MostrarDialogo("Se exporto satisfactoriamente");

                    try {
                        sendFile(dniSupervisor, txt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    RestoreDatabase();
                    SharedPreferences.Editor editor = getSharedPreferences("personal", MODE_PRIVATE).edit();
                    editor.putBoolean("firstLoad", false);
                    editor.apply();
                } else {
                    MostrarDialogo("Error al exportar");
                }

            }
        });

        TextView tvSupervisor = findViewById(R.id.txtSupervisor);
        tvSupervisor.setText(sbSupervisor.toString());
        TextView tvCuadrilla = findViewById(R.id.tvCuadrilla);

        TextView tvFecha = findViewById(R.id.tvFecha);
        RestoreDatabase();
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private final OkHttpClient client = new OkHttpClient();

    public void sendFile(String user, File file) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "logo-square.png",
                        RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();

        Request request = new Request.Builder()
                .url("https://www.agrisoftweb.com/api/excel/importar?user=" + user)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
            if (result.getContents() != null) {
                txtQr.setText(result.getContents());
            } else {
                txtQr.setText("Error");
            }
    }

    private void RestoreDatabase() {
        records = myDatabase.QueryTable(dniSupervisor);
        table.removeAllViews();

        for (int i = 0; i < records.size(); i++) {
            final TableRow row = (TableRow) LayoutInflater.from(MainActivity.this).inflate(R.layout.attrib_row, null);
            ((TextView) row.findViewById(R.id.tdId)).setText(String.valueOf(records.get(i).id_costo));
            ((TextView) row.findViewById(R.id.tdDate)).setText(String.valueOf(records.get(i).fecha));
            ((TextView) row.findViewById(R.id.tdPersonal)).setText(records.get(i).personal);
            ((TextView) row.findViewById(R.id.tdIdPersonal)).setText(records.get(i).id_personal);
            ((TextView) row.findViewById(R.id.tdWorkerZone)).setText(records.get(i).zonatrabajo);
            ((TextView) row.findViewById(R.id.tdIdWorkerZone)).setText(records.get(i).id_zonatrabajo);
            ((TextView) row.findViewById(R.id.tdActivity)).setText(records.get(i).actividad);
            ((TextView) row.findViewById(R.id.tdIdActivity)).setText(records.get(i).id_actividad);
            ((TextView) row.findViewById(R.id.tdHour)).setText(records.get(i).horas);
            ((TextView) row.findViewById(R.id.tdAdvance)).setText(records.get(i).avance);
            ((TextView) row.findViewById(R.id.tdQR)).setText(records.get(i).qr);

            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    resetTableStyle();

                    TableRow t = (TableRow) view;
                    TextView firstTextView = (TextView) t.getChildAt(0);
                    String firstText = firstTextView.getText().toString();

                    if (current_idCosto.equals(firstText)) {
                        current_idCosto = "";
                        selectRow("0", new Date().toString(), "0",
                                "0", "", "", "");
                        spWorker.setSelection(0);
                        spWorkerZone.setSelection(0);
                        spActivity.setSelection(0);
                        return;
                    }

                    current_idCosto = firstText;

                    selectRow(((TextView) t.getChildAt(2)).getText().toString(),
                            ((TextView) t.getChildAt(1)).getText().toString(),
                            ((TextView) t.getChildAt(4)).getText().toString(),
                            ((TextView) t.getChildAt(6)).getText().toString(),
                            ((TextView) t.getChildAt(8)).getText().toString(),
                            ((TextView) t.getChildAt(9)).getText().toString(),
                            ((TextView) t.getChildAt(10)).getText().toString());

//                    Toast.makeText(getApplicationContext(), "value was " + firstText,
//                            Toast.LENGTH_LONG).show();
                    view.setBackgroundColor(Color.RED);
                }
            });

            table.addView(row);
        }

        table.requestLayout();
    }

    public void LLenar(Spinner sp, int type) {
        ArrayList<String> asp1 = new ArrayList<>();

        if (type == 1) {
            asp1.add("Seleccione trabajador");

            for (int i = 0; i < workers.size(); i++) {
                asp1.add(workers.get(i).descripcion);
            }
        }

        if (type == 2) {
            asp1.add("Seleccione Zona de Trabajo");


            for (int i = 0; i < workerZone.size(); i++) {
                asp1.add(workerZone.get(i).descripcion);
            }
        }

        if (type == 3) {
            asp1.add("Seleccione Actividad");

            for (int i = 0; i < activities.size(); i++) {
                asp1.add(activities.get(i).descripcion);
            }
        }

        if (type == 4) {
            asp1.add("Seleccione Condicion");

            for (int i = 0; i < listCondicion.size(); i++) {
                asp1.add(listCondicion.get(i).descripcion);
            }
        }

        if (type == 5) {
            asp1.add("Seleccione Clon");

            for (int i = 0; i < listClon.size(); i++) {
                asp1.add(listClon.get(i).descripcion);
            }
        }

        if (type == 6) {
            asp1.add("Seleccione Estado Fisico");

            for (int i = 0; i < listEstadoFisico.size(); i++) {
                asp1.add(listEstadoFisico.get(i).descripcion);
            }
        }

        if (type == 7) {
            asp1.add("Seleccione Estado Sanitario");

            for (int i = 0; i < listEstadoSanitario.size(); i++) {
                asp1.add(listEstadoSanitario.get(i).descripcion);
            }
        }

        if (type == 8) {
            asp1.add("Seleccione Estado Sitio");

            for (int i = 0; i < listEstadoSitio.size(); i++) {
                asp1.add(listEstadoSitio.get(i).descripcion);
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, asp1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp.setAdapter(adapter);
    }

    /*
    public void Dialogo(final int type) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo);
        final EditText text = (EditText) dialog.findViewById(R.id.eddes);

        Button dialogButton = (Button) dialog.findViewById(R.id.button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = text.getText().toString();

                if (desc.isEmpty()) {
                    MostrarDialogo("Ingrese Descripción");
                    return;
                }

                if (type == 1) {
                    myDatabase.insertRecordsClon("1", desc, 1);
                }

                if (type == 2) {

                    myDatabase.insertRecordsClon("1", desc, 2);

                }

                if (type == 3) {

                    myDatabase.insertRecordsClon("1", desc, 3);

                }

                if (type == 4) {

                    myDatabase.insertRecordsClon("1", desc, 4);

                }

                if (type == 5) {

                    myDatabase.insertRecordsClon("1", desc, 5);

                }

                patron = myDatabase.RestoreFromDbPatron();
                plagas = myDatabase.RestoreFromDbPlagas();
                actividades = myDatabase.RestoreFromDbActividades();
                zonatrabajo = myDatabase.RestoreFromDbZonaTrabajo();
                clon = myDatabase.RestoreFromDbClon();


                Log.d("patron", "" + patron.size());
                Log.d("plagas", "" + plagas.size());
                Log.d("actividades", "" + actividades.size());
                Log.d("zonatrabajo", "" + zonatrabajo.size());
                Log.d("clon", "" + clon.size());

                LLenar(sp1, 1);
                LLenar(sp2, 3);
                LLenar(sp3, 2);
                LLenar(sp4, 5);


                dialog.dismiss();
            }
        });

        dialog.show();

    }
*/
    public void MostrarDialogo(String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Mensaje")
                .setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void getFromDB() {
        verifyStoragePermissions();
        cuadrilla = myDatabase.getCuadrilla(dniSupervisor);
    }

    public void selectRow(String id_personal, String fecha, String id_workerZone, String id_actividad, String hour,
                          String advance, String QR) {
        fecha = fecha;
        selectSpinnerItemByValue(spWorker, id_personal);
        selectSpinnerItemByValue(spWorkerZone, id_workerZone);
        selectSpinnerItemByValue(spActivity, id_actividad);

        txtHora.setText(hour);
        txtAvance.setText(advance);
        txtQr.setText(QR);

    }

    public void resetTableStyle() {
        TableLayout layout = (TableLayout) findViewById(R.id.tabla);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                row.setBackgroundColor(Color.parseColor("#C8C4C4"));

//                for (int x = 0; x < row.getChildCount(); x++) {
//                    View view = row.getChildAt(x);
//                    view.setBackgroundColor(Color.BLUE);
//                }
            }
        }
    }

    public static void selectSpinnerItemByValue(Spinner spnr, String value) {
        for (int i = 0; i < spnr.getCount(); i++) {
            if (spnr.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spnr.setSelection(i);
                break;
            } else {
                spnr.setSelection(0);
            }
        }
    }

    public String getWorkerId(String value) {
        for (int i = 0; i < workers.size(); i++) {
            if (workers.get(i).descripcion == value) {
                return workers.get(i).id;
            }
        }

        return "";
    }

    public String getWorkerZoneId(String value) {
        for (int i = 0; i < workerZone.size(); i++) {
            if (workerZone.get(i).descripcion.equals(value)) return workerZone.get(i).id;
        }

        return "";
    }

    public String getActivityId(String value) {
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).descripcion.equals(value)) return activities.get(i).id;
        }

        return "";
    }
}

