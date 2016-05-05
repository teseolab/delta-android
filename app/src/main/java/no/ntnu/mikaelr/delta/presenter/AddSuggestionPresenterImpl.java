package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.fragment.AddImageDialog;
import no.ntnu.mikaelr.delta.fragment.CustomDialog;
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

public class AddSuggestionPresenterImpl implements AddSuggestionPresenter,
        ProjectInteractorImpl.OnPostSuggestionListener, ProjectInteractorImpl.OnPostImageListener {

    private AddSuggestionView view;
    private Activity context;
    private ProjectInteractor interactor;
    private int projectId;

    private Uri imageFromCameraUri;
    private byte[] imageByteArray;
    private final String NO_IMAGE_DIALOG_TAG = "NO_IMAGE_DIALOG_TAG";
    private final String CANCEL_DIALOG_TAG = "CANCEL_DIALOG_TAG";

    public AddSuggestionPresenterImpl(AddSuggestionView view) {
        this.view = view;
        this.context = (Activity) view;
        this.interactor = new ProjectInteractorImpl();
        this.projectId = context.getIntent().getIntExtra("projectId", -1);
    }

    // INTERFACE METHODS

    @Override
    public void onDoneClick(String title, String details) {
        if (validateSuggestion(title, details)) {
            if (imageByteArray != null) {
                view.showSpinner(true);
                interactor.uploadImage(imageByteArray, this);
            } else {
                CustomDialog dialog = CustomDialog.newInstance("Forslag uten bilde", "Er du sikker på at du vil poste et forslag uten bilde?", "Post forslag", "Legg til bilde", 0);
                view.showDialog(dialog, NO_IMAGE_DIALOG_TAG);
            }
        }
    }

    @Override
    public void onCancelClick(String details) {
        if (!details.equals("")) {
            CustomDialog dialog = CustomDialog.newInstance("Avbryte forslag", "Er du sikker på at du vil avbryte?", "Ja", "Nei", 0);
            view.showDialog(dialog, CANCEL_DIALOG_TAG);
        } else {
            context.finish();
        }
    }

    private boolean validateSuggestion(String title, String details) {
        if (title.equals("")) {
            view.showMessage(ErrorMessage.TITLE_CANNOT_BE_EMPTY, Toast.LENGTH_SHORT);
            return false;
        } else if (details.equals("")) {
            view.showMessage(ErrorMessage.SUGGESTION_CANNOT_BE_EMPTY, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
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

    @Override
    public void onNegativeButtonClick(String dialogTag) {
        if (dialogTag.equals(NO_IMAGE_DIALOG_TAG)) {
            view.showDialog(AddImageDialog.newInstance(), null);
        }
    }

    @Override
    public void onPositiveButtonClick(String dialogTag) {
        if (dialogTag.equals(NO_IMAGE_DIALOG_TAG)) {
            postSuggestion("");
        } else if (dialogTag.equals(CANCEL_DIALOG_TAG)) {
            context.finish();
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
        view.showMessage(ErrorMessage.COULD_NOT_POST_SUGGESTION, Toast.LENGTH_LONG);
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
