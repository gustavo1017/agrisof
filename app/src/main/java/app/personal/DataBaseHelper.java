package app.personal;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import androidx.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;

public class DataBaseHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "sqlite.db";
    private static final int DATABASE_VERSION = 1;

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    public void Leer(Context context) {
        //insert
    }


    public void LeerInicial(Context context) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from PERSONAL");
        db.execSQL("delete from ZONA_TRABAJO");
        db.execSQL("delete from ACTIVIDADES");
        db.execSQL("delete from CUADRILLA");
        db.execSQL("delete from CONDICION");
        db.execSQL("delete from ESTADO_FISICO");
        db.execSQL("delete from ESTADO_SANITARIO");
        db.execSQL("delete from ESTADO_SITIO");
        db.execSQL("delete from CLON");
        db.execSQL("delete from COSTOS WHERE saved=0 AND deleted = 0");
        db.execSQL("delete from INVENTARIOPLANTAS WHERE saved=0 AND deleted = 0");

        //insert
        readExcelFile(context, "analisis1", 4); // ZONA TRABAJO
        readExcelFile(context, "analisis1", 6); // ACTIVIDADES
        readExcelFile(context, "analisis1", 8); // TRABAJADORES

        readExcelFile(context, "analisis1", 2); // CLON
        readExcelFile(context, "analisis1", 11); // CONDICION
        readExcelFile(context, "analisis1", 12); //  ESTADO FISICO
        readExcelFile(context, "analisis1", 13); // ESTADO SANITARIO
        readExcelFile(context, "analisis1", 14); // ESTADO SITIO


        readExcelFile(context, "analisis1", 9); // CUADRILLA


    }

    public void readPlanificacion(Context context) {
        readExcelFile(context, "analisis1", 10); // PLANIFICACION
    }


    public boolean writeExcelFile(int type, String dni_supervisor) {
        boolean success = false;

        try {
            HSSFWorkbook wb = null;

            try {
                wb = new HSSFWorkbook();

                Cell c = null;

                //Cell style for header row
                CellStyle cs = wb.createCellStyle();
                cs.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
                cs.setFillForegroundColor(HSSFColor.LIME.index);
                // cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                //New Sheet
                Sheet sheet1 = null;
                sheet1 = wb.createSheet("MapeoPersonal");

                // Generate column headings
                Row row = sheet1.createRow(0);

                c = row.createCell(0);
                c.setCellValue("Numero_Parte");
                c.setCellStyle(cs);

                c = row.createCell(1);
                c.setCellValue("Fecha");
                c.setCellStyle(cs);

                c = row.createCell(2);
                c.setCellValue("ID_Producto");
                c.setCellStyle(cs);

                c = row.createCell(3);
                c.setCellValue("ID_Maquinaria");
                c.setCellStyle(cs);

                c = row.createCell(4);
                c.setCellValue("ID_Personal");
                c.setCellStyle(cs);

                c = row.createCell(5);
                c.setCellValue("ID_Actividad");
                c.setCellStyle(cs);

                c = row.createCell(6);
                c.setCellValue("ID_Zonatrabajo");
                c.setCellStyle(cs);

                c = row.createCell(7);
                c.setCellValue("ID_Proveedor");
                c.setCellStyle(cs);

                c = row.createCell(8);
                c.setCellValue("cantidad");
                c.setCellStyle(cs);

                c = row.createCell(9);
                c.setCellValue("Costo_Unitario_Standar");
                c.setCellStyle(cs);

                c = row.createCell(10);
                c.setCellValue("monto");
                c.setCellStyle(cs);

                c = row.createCell(11);
                c.setCellValue("Tipo_Costo");
                c.setCellStyle(cs);

                c = row.createCell(12);
                c.setCellValue("Observaciones");
                c.setCellStyle(cs);

                c = row.createCell(13);
                c.setCellValue("Campana");
                c.setCellStyle(cs);

                c = row.createCell(14);
                c.setCellValue("Tipo_Cambio");
                c.setCellStyle(cs);

                c = row.createCell(15);
                c.setCellValue("id_Enlace");
                c.setCellStyle(cs);

                c = row.createCell(16);
                c.setCellValue("QR");
                c.setCellStyle(cs);

                c = row.createCell(17);
                c.setCellValue("ID_Cuadrilla");
                c.setCellStyle(cs);

                sheet1.setColumnWidth(0, (15 * 500));
                sheet1.setColumnWidth(1, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));


                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT \n" +
                        "\tC.fecha, \n" +
                        "\tC.id_personal,\n" +
                        "\tC.id_actividad,\n" +
                        "\tC.id_zonatrabajo,\n" +
                        "\tC.cantidad,\n" +
                        "\tC.id_enlace,\n" +
                        "\tC.QR,\n" +
                        "\tC.id_cuadrilla\n" +
                        "\tFROM COSTOS C\n" +
                        "\tINNER JOIN CUADRILLA CU ON C.id_cuadrilla = CU.id_cuadrilla\n" +
                        "\tWHERE CU.id_personal = '" + dni_supervisor + "'" +
                        "\tAND C.deleted = 0";

                Cursor cur = db.rawQuery(query, null);

                int i = 1;
                if (cur.moveToFirst()) {
                    do {
                        Row row1 = sheet1.createRow(i);

                        String date = cur.getString(0);
                        String[] dateList = date.split("/");
                        c = row1.createCell(0);
                        c.setCellValue(dateList[0] + "-" + dateList[1] + "-" + dateList[2]);
                        c.setCellStyle(cs);

                        c = row1.createCell(1);
                        c.setCellValue(cur.getString(0));
                        c.setCellStyle(cs);

                        c = row1.createCell(2);
                        c.setCellValue("000001");
                        c.setCellStyle(cs);

                        c = row1.createCell(13);
                        c.setCellValue("000001");
                        c.setCellStyle(cs);

                        c = row1.createCell(4);
                        c.setCellValue(cur.getString(1));
                        c.setCellStyle(cs);

                        c = row1.createCell(5);
                        c.setCellValue(cur.getString(2));
                        c.setCellStyle(cs);

                        c = row1.createCell(6);
                        c.setCellValue(cur.getString(3));
                        c.setCellStyle(cs);

                        c = row1.createCell(7);
                        c.setCellValue("000001");
                        c.setCellStyle(cs);

                        c = row1.createCell(8);
                        c.setCellValue(cur.getString(4));
                        c.setCellStyle(cs);

                        c = row1.createCell(9);
                        c.setCellValue("1");
                        c.setCellStyle(cs);

                        c = row1.createCell(10);
                        c.setCellValue("1");
                        c.setCellStyle(cs);

                        c = row1.createCell(11);
                        c.setCellValue("H");
                        c.setCellStyle(cs);

                        c = row1.createCell(12);
                        c.setCellValue("app");
                        c.setCellStyle(cs);

                        c = row1.createCell(13);
                        c.setCellValue("1");
                        c.setCellStyle(cs);

                        c = row1.createCell(14);
                        c.setCellValue("1");
                        c.setCellStyle(cs);

                        c = row1.createCell(15);
                        c.setCellValue(cur.getString(5));
                        c.setCellStyle(cs);

                        c = row1.createCell(16);
                        c.setCellValue(cur.getString(6));
                        c.setCellStyle(cs);

                        c = row1.createCell(17);
                        c.setCellValue(cur.getString(7));
                        c.setCellStyle(cs);

                        sheet1.setColumnWidth(0, (15 * 500));
                        sheet1.setColumnWidth(1, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));
                        sheet1.setColumnWidth(2, (15 * 500));

                        i++;
                    } while (cur.moveToNext());
                }

                cur.close();
                db.close();
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                String name = "tareo";

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String fileName = name + formatter.format(new Date());
                File file = new File(extStorageDirectory, "personal/" + fileName + ".xls");

                writeToFile("File content".getBytes(), file);

                FileOutputStream os = null;

                try {
                    os = new FileOutputStream(file);
                    wb.write(os);
                    Log.w("FileUtils", "Writing file" + file);
                    success = true;
                } catch (IOException e) {
                    Log.w("FileUtils", "Error writing " + file, e);
                } catch (Exception e) {
                    Log.w("FileUtils", "Failed to save file", e);
                } finally {
                    try {
                        if (null != os)
                            os.close();
                    } catch (Exception ex) {
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        deleteAllInventarioPlantas();
        return success;
    }

    public File writeTXTFile(String dni_supervisor) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT \n" +
                    "\tI.fecha, \n" +
                    "\tI.id_zonatrabajo,\n" +
                    "\tI.linea,\n" +
                    "\tI.id_clon,\n" +
                    "\tI.id_condicion,\n" +
                    "\tI.id_estadofisico,\n" +
                    "\tI.id_estadosanitario,\n" +
                    "\tI.id_estadositio,\n" +
                    "\tI.observaciones,\n" +
                    "\tI.fechaauditoria,\n" +
                    "\tI.QR\n"  +
                    "\tFROM INVENTARIOPLANTAS I\n"  +
                    "\tWHERE I.deleted = 0";

            Cursor cur = db.rawQuery(query, null);

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Fecha");
            stringBuilder.append("\t");
            stringBuilder.append("ID_zonatrabajo");
            stringBuilder.append("\t");
            stringBuilder.append("linea");
            stringBuilder.append("\t");
            stringBuilder.append("ID_clon");
            stringBuilder.append("\t");
            stringBuilder.append("ID_condicion");
            stringBuilder.append("\t");
            stringBuilder.append("ID_estadofisico");
            stringBuilder.append("\t");
            stringBuilder.append("ID_estadosanitario");
            stringBuilder.append("\t");
            stringBuilder.append("ID_estadositio");
            stringBuilder.append("\t");
            stringBuilder.append("Observaciones");
            stringBuilder.append("\t");
            stringBuilder.append("fechaauditoria");
            stringBuilder.append("\t");
            stringBuilder.append("QR");
            stringBuilder.append("\t");


            int i = 1;
            if (cur.moveToFirst()) {
                do {

                        stringBuilder.append("\n");

                        String date = cur.getString(0);
                        String[] dateList = date.split("/");


                        stringBuilder.append(dateList[0] + "-" + dateList[1] + "-" + dateList[2]);
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(1).trim());
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(2).trim());
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(3).trim());

                        stringBuilder.append(cur.getString(4).trim());
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(5).trim());
                        stringBuilder.append("\t");
                        stringBuilder.append("\t");
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(6).trim());
                        stringBuilder.append("\t");
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(7).trim());
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(8).trim());
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(9).trim());
                        stringBuilder.append("\t");

                        stringBuilder.append(cur.getString(10).trim());
                        stringBuilder.append("\t");




                    i++;
                } while (cur.moveToNext());
            }

            cur.close();
            db.close();

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            String name = "tareo";

            SimpleDateFormat formatterHour = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = formatterHour.format(new Date()) + name + formatterDate.format(new Date());
            File file = new File(extStorageDirectory, "personal/" + fileName + ".txt");

            writeToFile(stringBuilder.toString().getBytes(), file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean rowValidation(String fecha, String id_personal, String id_actividad,
                                  String id_zonatrabajo, String cantidad, String id_enlace) {

        boolean rtn = false;

        if (fecha.isEmpty()) rtn = true;
        if (id_personal.isEmpty()) rtn = true;
        if (id_actividad.isEmpty()) rtn = true;
        if (id_zonatrabajo.isEmpty()) rtn = true;
        if (cantidad.isEmpty()) rtn = true;
        if (id_enlace.isEmpty()) rtn = true;

        return rtn;
    }

    public boolean deleteAllInventarioPlantas() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM INVENTARIOPLANTAS");
        return true;
    }

    private void readExcelFile(Context context, String filename, int type) {
        try {
            // Creating Input Stream
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            // File file = new File(extStorageDirectory, "personal/analisis1.xls");

            //   File file = new File(context.getExternalFilesDir(null), filename);
            // FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            // POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);


            // OPCPackage pkg = OPCPackage.open(extStorageDirectory + "personal/analisis1.xls");

            Workbook myWorkBook = WorkbookFactory.create(new File(extStorageDirectory, "personal/analisis1.xls"));

            // Create a workbook using the File System
            // HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);


//            InputStream ExcelFileToRead = new FileInputStream(extStorageDirectory + "/personal/analisis1.xls");
            // XSSFWorkbook myWorkBook = new XSSFWorkbook(pkg);


            // HSSFSheet sheet = myWorkBook.getSheetAt(0);
            XSSFSheet sheet = (XSSFSheet) myWorkBook.getSheetAt(0);

            Iterator rows = sheet.rowIterator();

            Sheet mySheet = null;
            // Get the first sheet from workbook
            if (type == 1) {
                mySheet = myWorkBook.getSheetAt(0);
            }

            if (type == 2) {
                mySheet = myWorkBook.getSheetAt(1);
            }

            if (type == 3) {
                mySheet = myWorkBook.getSheetAt(2);
            }

            if (type == 4) {
                mySheet = myWorkBook.getSheetAt(3);
            }

            if (type == 5) {
                mySheet = myWorkBook.getSheetAt(4);
            }

            if (type == 6) {
                mySheet = myWorkBook.getSheetAt(5);
            }

            if (type == 7) {
                mySheet = myWorkBook.getSheetAt(6);
            }

            if (type == 8) {
                mySheet = myWorkBook.getSheetAt(7);
            }

            if (type == 9) {
                mySheet = myWorkBook.getSheetAt(8);
                Iterator rowCuadrilla = mySheet.rowIterator();

                while (rowCuadrilla.hasNext()) {
                    Row myRow = (Row) rowCuadrilla.next();
                    Iterator cellIter = myRow.cellIterator();

                    CuadrillaModel ob = new CuadrillaModel();

                    while (cellIter.hasNext()) {
                        Cell myCell = (Cell) cellIter.next();
                        // myCell.setCellType(Cell.CELL_TYPE_STRING);
                        myCell.setCellType(CellType.STRING);

                        if (!myCell.toString().isEmpty()) {
                            if (ob.id_cuadrilla == null) {
                                ob.id_cuadrilla = myCell.toString();
                            } else if (ob.descrption == null) {
                                ob.descrption = myCell.toString();
                            } else {
                                ob.id_personal = myCell.toString();
                            }
                        }
                    }

                    insertCuadrilla(ob.id_cuadrilla, ob.id_personal, ob.id_personal);
                }

                return;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

            if (type == 10) {
                mySheet = myWorkBook.getSheetAt(9);
                Iterator rowPlanification = mySheet.rowIterator();

                int count = 0;
                while (rowPlanification.hasNext()) {
                    Row myRow = (Row) rowPlanification.next();

                    if (count == 0) myRow = (Row) rowPlanification.next();
                    count += 1;
                    Iterator cellIter = myRow.cellIterator();
                    ObjectRecord ob = new ObjectRecord();

                    while (cellIter.hasNext()) {
                        Cell myCell = (Cell) cellIter.next();

                        if (!myCell.toString().isEmpty()) {
                            if (ob.id_cuadrilla == null) {
                                // myCell.setCellType(Cell.CELL_TYPE_STRING);
                                myCell.setCellType(CellType.STRING);
                                ob.id_cuadrilla = myCell.toString();
                            } else if (ob.fecha == null) {
                                myCell.setCellType(CellType.NUMERIC);
                                Date date = myCell.getDateCellValue();
                                ob.fecha = formatter.format(date);
                            } else if (ob.description == null) {
//                                myCell.setCellType(Cell.CELL_TYPE_STRING);
                                myCell.setCellType(CellType.STRING);
                                ob.description = myCell.toString();
                            } else if (ob.id_actividad == null) {
//                                myCell.setCellType(Cell.CELL_TYPE_STRING);
                                myCell.setCellType(CellType.STRING);
                                ob.id_actividad = myCell.toString();
                            } else if (ob.id_zonatrabajo == null) {
//                                myCell.setCellType(Cell.CELL_TYPE_STRING);
                                myCell.setCellType(CellType.STRING);
                                ob.id_zonatrabajo = myCell.toString();
                            } else if (ob.id_personal == null) {
//                                myCell.setCellType(Cell.CELL_TYPE_STRING);
                                myCell.setCellType(CellType.STRING);
                                ob.id_personal = myCell.toString();
                            }
                        }

                        Log.d("TAG", "Cell Value: " + myCell.toString());
                    }

                    insertMapeo(ob.id_cuadrilla, ob.fecha, ob.id_personal, ob.id_actividad, ob.id_zonatrabajo,
                            "", "", "");
                }

                return;
            }

            if (type == 11) {
                mySheet = myWorkBook.getSheetAt(10);
            }

            if (type == 12) {
                mySheet = myWorkBook.getSheetAt(11);
            }

            if (type == 13) {
                mySheet = myWorkBook.getSheetAt(12);
            }

            if (type == 14) {
                mySheet = myWorkBook.getSheetAt(13);
            }

            // We now need something to iterate through the cells.
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                Row myRow = (Row) rowIter.next();
                Iterator cellIter = myRow.cellIterator();

                ObjectAgro ob = new ObjectAgro();
                while (cellIter.hasNext()) {
                    Cell myCell = (Cell) cellIter.next();
//                    myCell.setCellType(Cell.CELL_TYPE_STRING);
                    myCell.setCellType(CellType.STRING);

                    if (!myCell.toString().isEmpty()) {
                        if (ob.id == null) {
                            ob.id = myCell.toString();
                        } else {
                            ob.descripcion = myCell.toString();
                        }
                    }

                    // Log.d("TAG", "Cell Value: " + myCell.toString());
                }

                if (ob.id != null) {
                    insertRecordsClon(ob.id, ob.descripcion, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public ArrayList<ObjectRecord> QueryTable(String dni_supervisor) {
        ArrayList<ObjectRecord> object = new ArrayList<ObjectRecord>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT\n" +
                "C.id_costo,\n" +
                "P.nombre as 'personal',\n" +
                "C.id_personal," +
                "ZT.descripcion as 'zona_trabajo',\n" +
                "C.id_zonatrabajo," +
                "A.descripcion as 'actividad',\n" +
                "C.id_actividad," +
                "C.cantidad as 'horas',\n" +
                "C.id_enlace as 'avance',\n" +
                "C.QR\n" +
                "FROM COSTOS C\n" +
                "LEFT JOIN PERSONAL P ON C.id_personal = P.id_personal\n" +
                "LEFT JOIN ZONA_TRABAJO ZT ON C.id_zonatrabajo = ZT.id_zonatrabajo\n" +
                "LEFT JOIN ACTIVIDADES A ON C.id_actividad = A.id_actividad\n" +
                "LEFT JOIN CUADRILLA CU ON C.id_cuadrilla = CU.id_cuadrilla\n" +
                "WHERE CU.id_personal = '" + dni_supervisor + "'" +
                "AND C.deleted = 0";

        Cursor cur = db.rawQuery(query, null);

        if (cur.moveToFirst()) {
            do {
                ObjectRecord record = new ObjectRecord();
                record.id_costo = cur.getInt(0);
                record.personal = cur.getString(1);
                record.id_personal = cur.getString(2);
                record.zonatrabajo = cur.getString(3);
                record.id_zonatrabajo = cur.getString(4);
                record.actividad = cur.getString(5);
                record.id_actividad = cur.getString(6);
                record.horas = cur.getString(7);
                record.avance = cur.getString(8);
                record.qr = cur.getString(9);

                object.add(record);
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();

        return object;
    }

    //MIO
    public ArrayList<ObjectRecordInventario> QueryTable2(String dni_supervisor) {
        ArrayList<ObjectRecordInventario> object = new ArrayList<ObjectRecordInventario>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT\n" +
                "I.id_IP,\n" +
                "ZT.descripcion as 'zona_trabajo',\n" +
                "I.id_zonatrabajo," +
                "EF.descripcion as 'estado_fisico',\n" +
                "I.id_estadofisico," +
                "EST.descripcion as 'estado_sanitario',\n" +
                "I.id_estadosanitario," +
                "ESI.descripcion as 'estado_sitio',\n" +
                "I.id_estadositio," +
                "CON.descripcion as 'condicion',\n" +
                "I.dt as 'dt',\n" +
                "I.linea as 'linea',\n" +
                "I.nro_arbol as 'nro_arbol',\n" +
                "I.id_edad as 'edad',\n" +
                "I.observaciones as 'obsevaciones',\n" +
                "I.QR\n" +
                "FROM INVENTARIOPLANTAS I\n" +
                "LEFT JOIN CLON C ON I.id_clon = C.id_clon\n" +
                "LEFT JOIN ZONA_TRABAJO ZT ON I.id_zonatrabajo = ZT.id_zonatrabajo\n" +
                "LEFT JOIN CONDICION CON ON I.id_condicion = CON.id_condicion\n" +
                "LEFT JOIN ESTADO_FISICO EF ON I.id_estadofisico = EF.id_estadofisico\n" +
                "LEFT JOIN ESTADO_SANITARIO EST ON I.id_estadosanitario = EST.id_estadosanitario\n"  +
                "LEFT JOIN ESTADO_SITIO ESI ON I.id_estadositio = ESI.id_estadositio\n"  +
                "WHERE I.id_usuario = '" + "demo01" + "'" +
                "AND I.saved = 0";

        Cursor cur = db.rawQuery(query, null);

        if (cur.moveToFirst()) {
            do {
                ObjectRecordInventario record = new ObjectRecordInventario();
                record.id_IP = cur.getInt(0);
                record.zonatrabajo = cur.getString(1);
                record.id_zonatrabajo = cur.getString(2);
                record.estadofisico = cur.getString(3);
                record.id_estadofisico = cur.getString(4);
                record.estadosanitario = cur.getString(5);
                record.id_estadosanitario = cur.getString(6);
                record.estadositio = cur.getString(7);
                record.id_estadositio = cur.getString(8);
                record.condicion = cur.getString(9);
                record.id_condicion = cur.getString(10);
                record.dt = cur.getString(11);
                record.nro_linea = cur.getString(12);
                record.id_edad = cur.getString(13);
                record.observaciones = cur.getString(14);
                record.qr = cur.getString(15);

                object.add(record);
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();

        return object;
    }

    public void insertCuadrilla(String id_cuadrilla, String description, String id_personal) {
        Log.e("test", id_cuadrilla);
        Log.e("test", description);
        Log.e("test", id_personal);

        SQLiteDatabase db = getWritableDatabase();
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id_cuadrilla", id_cuadrilla);
        nuevoRegistro.put("descripcion", description);
        nuevoRegistro.put("id_personal", id_personal);
        nuevoRegistro.put("ID", id_cuadrilla);

        db.insertWithOnConflict("CUADRILLA", null, nuevoRegistro,
                SQLiteDatabase.CONFLICT_REPLACE);
    }


    public void insertMapeo(String id_cuadrilla, String fecha, String id_personal, String id_actividad,
                             String id_zonatrabajo, String horas, String avance, String qr) {
        SQLiteDatabase db = getWritableDatabase();


        String query = "SELECT count(id_costo) FROM COSTOS\n" +
                "WHERE fecha = '" + fecha + "'\n" +
                "AND id_cuadrilla = '" + id_cuadrilla + "'\n" +
                "AND id_personal = '" + id_personal + "'\n" +
                "AND id_actividad = '" + id_actividad + "'\n" +
                "AND id_zonatrabajo = '" + id_zonatrabajo + "'\n" +
                "AND deleted = 1\n" +
                "OR (fecha = '" + fecha + "'\n" +
                "AND id_cuadrilla = '" + id_cuadrilla + "'\n" +
                "AND id_personal = '" + id_personal + "'\n" +
                "AND id_actividad = '" + id_actividad + "'\n" +
                "AND id_zonatrabajo = '" + id_zonatrabajo + "')";

        int result = 0;
        Cursor cur = db.rawQuery(query, null);
        if (cur.moveToFirst()) {
            do {
                result = cur.getInt(0);

            } while (cur.moveToNext());
        }
        cur.close();

        if (result > 0) return;

        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("fecha", fecha);
        nuevoRegistro.put("id_producto", "");
        nuevoRegistro.put("id_maquinaria", "");
        nuevoRegistro.put("id_cuadrilla", id_cuadrilla);
        nuevoRegistro.put("id_personal ", id_personal);
        nuevoRegistro.put("id_actividad", id_actividad);
        nuevoRegistro.put("id_zonatrabajo", id_zonatrabajo);
        nuevoRegistro.put("numero_doc", "");
        nuevoRegistro.put("cantidad", horas);
        nuevoRegistro.put("costo_unitario_standar", "");
        nuevoRegistro.put("tipo_costo", "");
        nuevoRegistro.put("observaciones", "");
        nuevoRegistro.put("campana", "");
        nuevoRegistro.put("Tipo_Cambio", "");
        nuevoRegistro.put("id_almacen", "");
        nuevoRegistro.put("id_enlace", avance);
        nuevoRegistro.put("id_usuario", "demo01");
        nuevoRegistro.put("fechaauditoria", new Date().toString());
        nuevoRegistro.put("QR", qr);

        // db.insert("COSTOS", null, nuevoRegistro);
        db.insertWithOnConflict("COSTOS", null, nuevoRegistro,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    //MIO
    public void insertMapeo2(String fecha, String dt, String id_zonatrabajo,String id_estadofisico,
                             String nroLinea, String id_estadosanitario,
                             String nroArbol, String id_condicion,String id_estadositio, String edad,
                             String id_clon, String observacion,String qr) {
        SQLiteDatabase db = getWritableDatabase();


        String query = "SELECT count(id_IP) FROM INVENTARIOPLANTAS\n" +
                "WHERE fecha = '" + fecha + "'\n" +
                "AND id_estadofisico = '" + id_estadofisico + "'\n" +
                "AND id_estadosanitario = '" + id_estadosanitario + "'\n" +
                "AND id_estadositio = '" + id_estadositio + "'\n" +
                "AND id_condicion = '" + id_condicion + "'\n" +
                "AND id_zonatrabajo = '" + id_zonatrabajo + "'\n" +
                "AND deleted = 1\n" +
                "OR (fecha = '" + fecha + "'\n" +
                "AND id_estadofisico = '" + id_estadofisico + "'\n" +
                "AND id_estadosanitario = '" + id_estadosanitario + "'\n" +
                "AND id_estadositio = '" + id_estadositio + "'\n" +
                "AND id_condicion = '" + id_condicion + "'\n" +
                "AND id_zonatrabajo = '" + id_zonatrabajo + "')";

        int result = 0;
        Cursor cur = db.rawQuery(query, null);
        if (cur.moveToFirst()) {
            do {
                result = cur.getInt(0);

            } while (cur.moveToNext());
        }
        cur.close();

        if (result > 0) return;

        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("fecha", fecha);
        nuevoRegistro.put("id_zonatrabajo", id_zonatrabajo);
        nuevoRegistro.put("linea", 0);
        nuevoRegistro.put("id_clon", id_clon);
        nuevoRegistro.put("nro_arbol ", 0);
        nuevoRegistro.put("id_condicion", id_condicion);
        nuevoRegistro.put("id_edad", 2.01);
        nuevoRegistro.put("dt", 10);
        nuevoRegistro.put("id_estadofisico", id_estadofisico);
        nuevoRegistro.put("id_estadosanitario", id_estadosanitario);
        nuevoRegistro.put("id_estadositio", id_estadositio);
        nuevoRegistro.put("observaciones", observacion);
        nuevoRegistro.put("id_usuario", "demo01");
        nuevoRegistro.put("fechaauditoria", new Date().toString());
        nuevoRegistro.put("QR", qr);

        // db.insert("COSTOS", null, nuevoRegistro);
        long b = db.insertWithOnConflict("INVENTARIOPLANTAS", null, nuevoRegistro,
                SQLiteDatabase.CONFLICT_REPLACE);
        int a = 0;
    }

    public void updateMapeo(String id_costo, String id_cuadrilla, String fecha, String id_personal,
                            String id_actividad, String id_zonatrabajo, String horas,
                            String avance, String qr) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("fecha", fecha);
        nuevoRegistro.put("id_producto", "");
        nuevoRegistro.put("id_maquinaria", "");
        nuevoRegistro.put("id_cuadrilla", id_cuadrilla);
        nuevoRegistro.put("id_personal ", id_personal);
        nuevoRegistro.put("id_actividad", id_actividad);
        nuevoRegistro.put("id_zonatrabajo", id_zonatrabajo);
        nuevoRegistro.put("numero_doc", "");
        nuevoRegistro.put("cantidad", horas);
        nuevoRegistro.put("costo_unitario_standar", "");
        nuevoRegistro.put("tipo_costo", "");
        nuevoRegistro.put("observaciones", "");
        nuevoRegistro.put("campana", "");
        nuevoRegistro.put("Tipo_Cambio", "");
        nuevoRegistro.put("id_almacen", "");
        nuevoRegistro.put("id_enlace", avance);
        nuevoRegistro.put("id_usuario", "DEMO01");
        nuevoRegistro.put("fechaauditoria", new Date().toString());
        nuevoRegistro.put("QR", qr);

        db.update("COSTOS", nuevoRegistro, "id_costo =" + id_costo, null);
    }

    public void updateMapeo2(String id_IP,String fecha, String dt, String id_zonatrabajo,String id_estadofisico,
                             String nroLinea, String id_estadosanitario,
                             String nroArbol, String id_condicion,String id_estadositio, String edad,
                             String id_clon, String observacion,String qr) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("fecha", fecha);
        nuevoRegistro.put("id_zonatrabajo", id_zonatrabajo);
        nuevoRegistro.put("linea", 0);
        nuevoRegistro.put("id_clon", id_clon);
        nuevoRegistro.put("nro_arbol ", 0);
        nuevoRegistro.put("id_condicion", id_condicion);
        nuevoRegistro.put("id_edad", 2.01);
        nuevoRegistro.put("dt", 10);
        nuevoRegistro.put("id_estadofisico", id_estadofisico);
        nuevoRegistro.put("id_estadosanitario", id_estadosanitario);
        nuevoRegistro.put("id_estadositio", id_estadositio);
        nuevoRegistro.put("observaciones", observacion);
        nuevoRegistro.put("id_usuario", "DEMO01");
        nuevoRegistro.put("fechaauditoria", new Date().toString());
        nuevoRegistro.put("QR", qr);

        db.update("INVENTARIOPLANTAS", nuevoRegistro, "id_IP =" + id_IP, null);
    }

    public void deleteMapeo(String id_IP) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("deleted", 1);

        long a = db.update("INVENTARIOPLANTAS", nuevoRegistro, "id_IP=" + id_IP, null);
    }

    void saveTablePermanent(String dni_supervisor) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE\n" +
                "INVENTARIOPLANTAS \n" +
                "SET saved=1\n" +
                "WHERE \n" +
                "id_usuario =  '" + "demo01" + "';";

        db.execSQL(query);
    }

    // MI codigo

    void saveTablePermanent2(String dni_supervisor) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE\n" +
                "COSTOS \n" +
                "SET saved=1\n" +
                "WHERE \n" +
                "id_cuadrilla = (SELECT id_cuadrilla\n" +
                "from CUADRILLA WHERE id_personal = '" + dni_supervisor + "');";

        db.execSQL(query);
    }

    private void insertRecordsClon(String id, String descripcion, int type) {
        SQLiteDatabase db = getWritableDatabase();

        if (type == 1) {
            ContentValues nuevoRegistro = new ContentValues();
            //nuevoRegistro.put("id_plaga", id);
            nuevoRegistro.put("descripcion", descripcion);
            db.insert("PLAGAS", null, nuevoRegistro);
        }

        if (type == 2) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_clon", id);
            nuevoRegistro.put("descripcion", descripcion);
            db.insert("CLON", null, nuevoRegistro);
        }

        if (type == 3) {
            ContentValues nuevoRegistro = new ContentValues();
            //nuevoRegistro.put("id_patron", id);
            nuevoRegistro.put("descripcion", descripcion);
            db.insert("PATRON", null, nuevoRegistro);
        }

        if (type == 4) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_zonatrabajo", id);
            nuevoRegistro.put("descripcion", descripcion);
            db.insert("ZONA_TRABAJO", null, nuevoRegistro);
        }

        if (type == 5) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_actividad", id);
            nuevoRegistro.put("descripcion", descripcion);
            db.insert("ACTIVIDADES", null, nuevoRegistro);
        }

        if (type == 6) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_actividad", id);
            nuevoRegistro.put("descripcion", descripcion);

            db.insert("ACTIVIDADES", null, nuevoRegistro);
        }

        if (type == 8) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_personal", id);
            nuevoRegistro.put("nombre", descripcion);

            db.insert("PERSONAL", null, nuevoRegistro);
        }

        if (type == 11) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_condicion", id);
            nuevoRegistro.put("descripcion", descripcion);

            db.insert("CONDICION", null, nuevoRegistro);
        }

        if (type == 12) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_estadofisico", id);
            nuevoRegistro.put("descripcion", descripcion);

            db.insert("ESTADO_FISICO", null, nuevoRegistro);
        }

        if (type == 13) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_estadosanitario", id);
            nuevoRegistro.put("descripcion", descripcion);

            db.insert("ESTADO_SANITARIO", null, nuevoRegistro);
        }

        if (type == 14) {
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("id_estadositio", id);
            nuevoRegistro.put("descripcion", descripcion);

            db.insert("ESTADO_SITIO", null, nuevoRegistro);
        }
    }

    public ArrayList<ObjectAgro> RestoreFromDbClon() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_clon", "descripcion"};
//        String[] selectionArgs={categoryId+"",subjectId+"",yearId+""};
        Cursor cursor = db.query("CLON", columns, null, null, null, null, null);
//        Cursor cursor=db.query(MyDatabase.TABLE_NAME, columns, null,null, null, null, null);


        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_clon"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));


            response.add(object);
        }
        return response;
    }

    public ArrayList<ObjectAgro> RestoreFromDbActividades() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_actividad", "descripcion"};
        Cursor cursor = db.query("ACTIVIDADES", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_actividad"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

            response.add(object);
        }

        return response;
    }

    public ObjectAgro getCuadrilla(String dniSupervisor) {
        ObjectAgro response = new ObjectAgro();
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT id_cuadrilla,descripcion FROM CUADRILLA WHERE id_personal = '" + dniSupervisor + "'";

        Cursor cur = db.rawQuery(query, null);

        if (cur.moveToFirst()) {
            do {
                ObjectAgro record = new ObjectAgro();
                record.id = cur.getString(0);
                record.descripcion = cur.getString(1);

                response = record;
            } while (cur.moveToNext());
        }

        cur.close();
        db.close();

        return response;
    }

    public ArrayList<ObjectAgro> RestoreFromDbPersonal() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_personal", "nombre"};
        Cursor cursor = db.query("PERSONAL", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_personal"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("nombre"));

            response.add(object);
        }

        return response;
    }

    public ArrayList<ObjectAgro> RestoreFromDbZonaTrabajo() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_zonatrabajo", "descripcion"};
        Cursor cursor = db.query("ZONA_TRABAJO", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_zonatrabajo"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

            response.add(object);
        }

        return response;
    }

    public ArrayList<ObjectAgro> RestoreFromDbCondicion() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_condicion", "descripcion"};
        Cursor cursor = db.query("CONDICION", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_condicion"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

            response.add(object);
        }

        return response;
    }

    public ArrayList<ObjectAgro> RestoreFromDbEstadoFisico() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_estadofisico", "descripcion"};
        Cursor cursor = db.query("ESTADO_FISICO", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_estadofisico"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

            response.add(object);
        }

        return response;
    }

    public ArrayList<ObjectAgro> RestoreFromDbEstadoSanitario() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_estadosanitario", "descripcion"};
        Cursor cursor = db.query("ESTADO_SANITARIO", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_estadosanitario"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

            response.add(object);
        }

        return response;
    }

    public ArrayList<ObjectAgro> RestoreFromDbEstadoSitio() {
        ArrayList<ObjectAgro> response = new ArrayList<ObjectAgro>();

        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"id_estadositio", "descripcion"};
        Cursor cursor = db.query("ESTADO_SITIO", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ObjectAgro object = new ObjectAgro();
            object.id = cursor.getString(cursor.getColumnIndex("id_estadositio"));
            object.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

            response.add(object);
        }

        return response;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static void writeToFile(byte[] data, File file) throws IOException {

        BufferedOutputStream bos = null;

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void writeToFile(String data, Context context, String nameFile) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(nameFile, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String convertValueCell(Object value) {
        if (value.getClass().equals(String.class)) return String.valueOf(value);

        return value.toString();
    }
}
