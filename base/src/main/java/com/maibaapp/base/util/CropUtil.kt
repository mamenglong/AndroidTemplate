package com.maibaapp.base.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import com.yalantis.ucrop.model.AspectRatio
import com.yalantis.ucrop.view.CropImageView
import java.io.File

class CropCallBack {
    private var onCropSuccess: ((Pair<Uri, Int>) -> Unit)? = null
    private var onPickSuccess: ((Pair<Uri, Int>) -> Unit)? = null
    private var onCropFail: ((Pair<String, Int>) -> Unit)? = null
    private var onPickFail: ((Pair<String, Int>) -> Unit)? = null
    fun onCropSuccess(action: (Pair<Uri, Int>) -> Unit) {
        onCropSuccess = action
    }

    fun onCropSuccess(uri: Uri, requestCode: Int) {
        val pair = Pair(uri, requestCode)
        onCropSuccess?.invoke(pair)
    }

    fun onPickSuccess(action: (Pair<Uri, Int>) -> Unit) {
        onPickSuccess = action
    }

    fun onPickSuccess(uri: Uri, requestCode: Int) {
        val pair = Pair(uri, requestCode)
        onPickSuccess?.invoke(pair)
    }

    fun onCropFail(action: (Pair<String, Int>) -> Unit) {
        onCropFail = action
    }

    fun onCropFail(msg: String, requestCode: Int) {
        val pair = Pair(msg, requestCode)
        onCropFail?.invoke(pair)
    }

    fun onPickFail(action: (Pair<String, Int>) -> Unit) {
        onPickFail = action
    }

    fun onPickFail(msg: String, requestCode: Int) {
        val pair = Pair(msg, requestCode)
        onPickFail?.invoke(pair)
    }
}

fun registerCropCallBack(block: CropCallBack.() -> Unit) = CropCallBack().apply(block)

object CropUtil {
    val PICKPICTUREREQUESTCODE = 0x01
    val PICK_PICTURE_FOR_ICON_REQUEST_CODE = 0x02
    val PICKPICTUREREQUESTCODEFROMCAMERA = 0x03
    private var photoUri:Uri?=null
    fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?,
        context: FragmentActivity,
        cropCallBack: CropCallBack,
        isCrop: Boolean = true
    ) {
        onActivityResult(requestCode, resultCode, data, context, null, cropCallBack, isCrop)
    }

    fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?,
        fragment: Fragment,
        cropCallBack: CropCallBack,
        isCrop: Boolean = true
    ) {
        onActivityResult(
                requestCode,
                resultCode,
                data,
                fragment.requireContext(),
                fragment,
                cropCallBack,
                isCrop
        )
    }

    private var currentCropRequestCode = UCrop.REQUEST_CROP


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * @param context
     * @param isCrop
     * @param onCropSuccess
     * @param onPickSuccess
     */
    private fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?,
        context: Context,
        fragment: Fragment?,
        cropCallBack: CropCallBack,
        isCrop: Boolean
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                val resultUri: Uri? = UCrop.getOutput(data!!)
                resultUri?.let {
                    cropCallBack.onCropSuccess(it, currentCropRequestCode)
                } ?: kotlin.run {
                    Toast.makeText(context, "Cannot retrieve Crop image", Toast.LENGTH_SHORT)
                        .show()
                    cropCallBack.onCropFail("Cannot retrieve Crop image", currentCropRequestCode)
                }
                currentCropRequestCode = UCrop.REQUEST_CROP
            }else if (requestCode == currentCropRequestCode) {
                val selectedUri = photoUri ?: data!!.data
                selectedUri?.let {
                    cropCallBack.onPickSuccess(it, requestCode)
                    if (isCrop) {
                        startCrop(context, it, fragment)
                    } else {
                        currentCropRequestCode = UCrop.REQUEST_CROP
                    }
                } ?: kotlin.run {
                    Toast.makeText(context, "Cannot retrieve selected image", Toast.LENGTH_SHORT)
                        .show()
                    cropCallBack.onPickFail("Cannot retrieve selected image", requestCode)
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Toast.makeText(context, "${cropError?.message}", Toast.LENGTH_SHORT).show()
            cropCallBack.onCropFail("${cropError?.message}", requestCode)
        }
    }

    fun pickFromGallery(context: FragmentActivity, requestCode: Int = PICKPICTUREREQUESTCODE) {
        pickImageFromGallery(context, requestCode)
    }

    fun pickFromGallery(fragment: Fragment, requestCode: Int = PICKPICTUREREQUESTCODE) {
        pickImageFromGallery(fragment, requestCode)
    }
    fun pickFromCamera(fragment: Fragment, requestCode: Int = PICKPICTUREREQUESTCODEFROMCAMERA){
            //调用系统相机的意图
        val file = File(fragment.requireContext().getExternalFilesDir("photo"),"avatar.png")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoUri = FileProvider.getUriForFile(
                fragment.requireContext(),
                fragment.requireContext().packageName + ".FileProvider",
                file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        currentCropRequestCode = requestCode
        fragment.startActivityForResult(intent, requestCode)
    }
    private fun pickImageFromGallery(context: Any, requestCode: Int = PICKPICTUREREQUESTCODE) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
            .addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes =
            arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        currentCropRequestCode = requestCode
        photoUri =null
        when (context) {
            is Fragment -> {
                context.startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "选择图片"
                        ), requestCode
                )
            }
            is FragmentActivity -> {
                context.startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "选择图片"
                        ), requestCode
                )
            }
            else -> {
                throw IllegalArgumentException("context must be fragment or fragmentActivity")
            }
        }

    }

    private fun startCrop(context: Context, uri: Uri, fragment: Fragment? = null) {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.PNG)
        options.setCompressionQuality(100)
        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
        options.setMaxBitmapSize(640)
        // Aspect ratio options
        options.setAspectRatioOptions(
                4,
                AspectRatio("1:2", 1F, 2f),
                AspectRatio("3:4", 3f, 4f),
                AspectRatio(
                        "source",
                        CropImageView.DEFAULT_ASPECT_RATIO,
                        CropImageView.DEFAULT_ASPECT_RATIO
                ),
                AspectRatio("16:9", 16f, 9f),
                AspectRatio("1:1", 1f, 1f)
        )

        options.setMaxScaleMultiplier(5f)
        options.setImageToCropBoundsAnimDuration(666)
        options.setDimmedLayerColor((Color.parseColor("#b6000000")))
        options.setCircleDimmedLayer(false)
        options.setShowCropFrame(true)
        options.setCropGridStrokeWidth(2)
        options.setCropGridColor(Color.GREEN)
        options.setCropGridColumnCount(2)
        options.setCropGridRowCount(1)
        options.setStatusBarColor(Color.parseColor("#FFBABABA"))
        options.setToolbarColor(Color.parseColor("#FFFFFFFF"))
        options.setToolbarWidgetColor(Color.parseColor("#FF333333"))
        options.setRootViewBackgroundColor(Color.TRANSPARENT)
/*        options.setToolbarCropDrawable(R.drawable.your_crop_icon)
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon)
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.your_color_res))*/
        val destFile =  File(context.getExternalFilesDir("cache"), "destinationFileName.png")
        val dstUri=FileProvider.getUriForFile(
                context,
                context.packageName + ".FileProvider",
                destFile)
        if (!destFile.exists()){
            destFile.parentFile.mkdirs()
            destFile.createNewFile()
        }
        val uCrop = UCrop.of(
                uri,
                dstUri
        )
        uCrop.useSourceImageAspectRatio()
        fragment?.let {
            uCrop.withOptions(options).start(context, fragment)
        } ?: kotlin.run {
            uCrop.withOptions(options).start(context as FragmentActivity)
        }
    }
}