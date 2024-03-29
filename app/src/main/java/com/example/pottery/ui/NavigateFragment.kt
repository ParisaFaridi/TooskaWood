package com.example.pottery.ui


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pottery.BuildConfig
import com.example.pottery.R
import com.example.pottery.databinding.FragmentNavigateBinding
import com.example.pottery.room.FormulaDataBase
import com.example.pottery.viewModels.FormulaViewModel
import ir.androidexception.roomdatabasebackupandrestore.Backup
import ir.androidexception.roomdatabasebackupandrestore.Restore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class NavigateFragment : Fragment() {
    private lateinit var binding: FragmentNavigateBinding
    private val formulaViewModel: FormulaViewModel by activityViewModels()
    private val dir = Environment.getExternalStorageDirectory().toString() + "/TooskaWoodBackUp"
    private var dbFilePath: String? = null
    private var imagesPath:String?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNavigateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewInsert.setOnClickListener {
            findNavController().navigate(R.id.action_navigateFragment_to_addFormulaFragment)
        }

        binding.imageViewSearch.setOnClickListener {
            findNavController().navigate(R.id.action_navigateFragment_to_homeFragment)
        }
        binding.imageViewProducts.setOnClickListener {
            formulaViewModel.webViewURL = "https://www.tooskawood.ir/shop/"
            findNavController().navigate(R.id.action_navigateFragment_to_webViewFragment)
        }
        binding.imageViewGuide.setOnClickListener {
            formulaViewModel.webViewURL = "https://www.tooskawood.ir/tooskawood_glaze/"
            findNavController().navigate(R.id.action_navigateFragment_to_webViewFragment)
        }
        binding.imageViewTrainingClasses.setOnClickListener {
            formulaViewModel.webViewURL = "https://www.tooskawood.ir/pottery-academic/"
            findNavController().navigate(R.id.action_navigateFragment_to_webViewFragment)
        }
        binding.imageViewWorkShop.setOnClickListener {
            formulaViewModel.webViewURL = "https://www.tooskawood.ir/workshop/"
            findNavController().navigate(R.id.action_navigateFragment_to_webViewFragment)
        }
        binding.imageViewCalculator.setOnClickListener {
            findNavController().navigate(R.id.action_navigateFragment_to_calculatorFragment)
        }
        binding.imageViewAboutMe.setOnClickListener {
            findNavController().navigate(R.id.action_navigateFragment_to_aboutFragment)
        }
        binding.imageViewBook.setOnClickListener {
            formulaViewModel.webViewURL = "https://www.tooskawood.ir/books/"
            findNavController().navigate(R.id.action_navigateFragment_to_webViewFragment)
        }
        binding.imageViewArtist.setOnClickListener {
            formulaViewModel.webViewURL = "https://www.tooskawood.ir/visual-artists-of-iran/"
            findNavController().navigate(R.id.action_navigateFragment_to_webViewFragment)
        }
        binding.imageViewShowPlace.setOnClickListener {
            formulaViewModel.webViewURL = "https://www.tooskawood.ir/event/"
            findNavController().navigate(R.id.action_navigateFragment_to_webViewFragment)
        }

        binding.buttonBackup.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                getAccessibility()
                Toast.makeText(
                    requireContext(),
                    "لطفا ابتدا دسترسی مربوطه را به نرم افزار بدهید و سپس دوباره نسخه پشتیبان تهیه کنید",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                getBackup()
            }
        }
        binding.button2.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                getAccessibility()
                Toast.makeText(
                    requireContext(),
                    "لطفا ابتدا دسترسی مربوطه را به نرم افزار بدهید و سپس دوباره نسخه پشتیبان تهیه کنید",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                openDialog()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getAccessibility() {
        val uri: Uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
    }

    private fun getBackup() {
        Backup.Init()
            .database(FormulaDataBase.getDatabase(requireContext()))
            .path(dir)
            .fileName("TooskaWoodBackupFile.txt")
            .onWorkFinishListener { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "تهیه نسخه پشنتیبان موفقیت آمیز بود، این نسخه در پوشه TooskaWoodBackU در فایل ها قابل مشاهده و بازیابی است",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .execute()
        val files = context?.filesDir?.listFiles()
        for (i in formulaViewModel.getAllFormulas()!!) {
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") && it.name == "${i.imagePath}.jpg" }
                ?.map {
                    val bytes = it.readBytes()
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    saveImageToExternal(bmp, i.imagePath)
                }
        }
    }

    private fun saveImageToExternal(finalBitmap: Bitmap, imagePath: String) {
        val myDir = File("$dir/images")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val fName = "$imagePath.jpg"
        val file = File(myDir, fName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun restoreBackup() {
        Restore.Init()
            .database(FormulaDataBase.getDatabase(requireContext()))
            .backupFilePath(dbFilePath)
            .onWorkFinishListener { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "اطلاعات با موفقیت بازیابی شد ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .execute()
        saveBackUpImagesToInternalStorage()
    }
    private fun saveBackUpImagesToInternalStorage() {
        val file = imagesPath?.let { File(it) }
        val images = file?.listFiles()
        for (image in images!!) {
            try {
                val filePath = image.path
                val bitmap = BitmapFactory.decodeFile(filePath)
                context?.openFileOutput("${image.name}.jpg", Activity.MODE_PRIVATE).use { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream))
                        throw IOException("COULDN'T SAVE BITMAP")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun openDialog() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                dbFilePath = getRealPathFromURI(requireContext(), uri)
                imagesPath = dbFilePath?.split("TooskaWoodBackupFile.txt")?.get(0) + "images"
                restoreBackup()
            }
        }
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        when {
            // DocumentProvider
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    // ExternalStorageProvider
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        // This is for checking Main Memory
                        return if ("primary".equals(type, ignoreCase = true)) {
                            if (split.size > 1) {
                                Environment.getExternalStorageDirectory()
                                    .toString() + "/" + split[1]
                            } else {
                                Environment.getExternalStorageDirectory().toString() + "/"
                            }
                            // This is for checking SD Card
                        } else {
                            "storage" + "/" + docId.replace(":", "/")
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val fileName = getFilePath(context, uri)
                        if (fileName != null) {
                            return Environment.getExternalStorageDirectory()
                                .toString() + "/Download/" + fileName
                        }
                        var id = DocumentsContract.getDocumentId(uri)
                        if (id.startsWith("raw:")) {
                            id = id.replaceFirst("raw:".toRegex(), "")
                            val file = File(id)
                            if (file.exists()) return id
                        }
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        return getDataColumn(context, contentUri, null, null)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    private fun getFilePath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(
                uri, projection, null, null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}