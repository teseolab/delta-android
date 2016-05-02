package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.signature.AddSuggestionPresenter;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.ImageHandler;
import no.ntnu.mikaelr.delta.view.SuggestionListActivity;
import no.ntnu.mikaelr.delta.view.signature.AddSuggestionView;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class AddSuggestionPresenterImpl implements AddSuggestionPresenter, ProjectInteractorImpl.OnPostSuggestionListener, ProjectInteractorImpl.OnPostImageListener {

    private AddSuggestionView view;
    private Activity context;
    private ProjectInteractor interactor;
    private int projectId;

    private Uri imageFromCameraUri;
    private byte[] imageByteArray;

    public AddSuggestionPresenterImpl(AddSuggestionView view) {
        this.view = view;
        this.context = (Activity) view;
        this.interactor = new ProjectInteractorImpl();
        this.projectId = context.getIntent().getIntExtra("projectId", -1);
    }

    // INTERFACE METHODS

    @Override
    public void onDoneClick(String title, String details) {
        if (imageByteArray != null) {
            interactor.uploadImage(imageByteArray, this);
        } else {
            postSuggestion("");
        }
    }

    private void postSuggestion(String imageUri) {
        Suggestion suggestion = new Suggestion();
        suggestion.setTitle(view.getSuggestionTitle());
        suggestion.setDetails(view.getSuggestionDetails());
        suggestion.setDate(Calendar.getInstance().getTime());
        suggestion.setImageUri(imageUri);
        suggestion.setProjectId(projectId);
        interactor.postSuggestion(suggestion, this);
    }

    @Override
    public void openCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFromCameraUri = ImageHandler.getOutputImageFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFromCameraUri);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Bitmap image = null;

            if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                image = ImageHandler.decodeImageFromFilePath(imageFromCameraUri, 350);
                try {
                    image = ImageHandler.rotateImage(image, imageFromCameraUri.getPath());
                } catch (IOException e) {e.printStackTrace();}
            }
            else if (requestCode == Constants.CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE) {
                image = ImageHandler.decodeImageFromFileUri(context.getContentResolver(), data.getData(), 350);
                try {
                    image = ImageHandler.rotateImage(image, data.getData().toString()); // TODO: Rotation does not work
                } catch (IOException e) {e.printStackTrace();}
            }


            if (image != null) {
                view.setSuggestionImage(image);
                view.setButtonText("Endre bilde");
                imageByteArray = ImageHandler.byteArrayFromBitmap(image);
            }
        }
    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onPostSuggestionSuccess(JSONObject jsonSuggestion) {
        if (projectId != -1) {
            Intent intent = new Intent(context, SuggestionListActivity.class);
            intent.putExtra("projectId", projectId);
            context.startActivity(intent);
            context.finish();
        }
    }

    @Override
    public void onPostSuggestionError(int errorCode) {
        view.showSpinner(false);
        view.showMessage(ErrorMessage.COULD_NOT_POST_SUGGESTION);
    }

    @Override
    public void onPostImageSuccess(String uri) {
        postSuggestion(uri);
    }

    @Override
    public void onPostImageError(int errorCode) {
        System.out.println("Image upload failed with error code " + errorCode);
    }
}
