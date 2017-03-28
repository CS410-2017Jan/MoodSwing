package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.DeleteCaptureUsecase;
import com.moodswing.mvp.domain.FollowUsecase;
import com.moodswing.mvp.domain.GetEntryPicUsecase;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.domain.GetProfilePictureUsecase;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.domain.SearchUsecase;
import com.moodswing.mvp.domain.SetTitleUsecase;
import com.moodswing.mvp.domain.UnfollowUsecase;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;
import com.moodswing.mvp.mvp.presenter.JournalPresenterOther;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;


@Module2
public class JournalModuleOther {

    @PerActivity
    @Provides2
    public GetJournalsUsecase provideGetJournalsUsecase(Repository repository) {
        return new GetJournalsUsecase(repository);
    }

    @PerActivity
    @Provides2
    public DeleteCaptureUsecase provideDeleteCaptureUsecase(Repository repository) {
        return new DeleteCaptureUsecase(repository);
    }

    @PerActivity
    @Provides2
    public SetTitleUsecase provideSetTitleUsecase(Repository repository) {
        return new SetTitleUsecase(repository);
    }

    @PerActivity
    @Provides2
    public SearchUsecase provideSearchUsecase(Repository repository) {
        return new SearchUsecase(repository);
    }

    @PerActivity
    @Provides2
    public GetProfilePictureUsecase provideGetProfilePictureUsecase(Repository repository) {
        return new GetProfilePictureUsecase(repository);
    }

    @PerActivity
    @Provides2
    public GetEntryPicUsecase provideGetEntryPicUsecase(Repository repository) {
        return new GetEntryPicUsecase(repository);
    }

    @PerActivity
    @Provides2
    public GetUserUsecase provideGetUserUsecase(Repository repository) {
        return new GetUserUsecase(repository);
    }

    @PerActivity
    @Provides2
    public FollowUsecase provideFollowUsecase(Repository repository) {
        return new FollowUsecase(repository);
    }

    @PerActivity
    @Provides2
    public UnfollowUsecase provideUnfollowUsecase(Repository repository) {
        return new UnfollowUsecase(repository);
    }

    @PerActivity
    @Provides2
    public JournalPresenterOther provideJournalPresenterOther(GetJournalsUsecase getJournalsUsecase,
                                                              DeleteCaptureUsecase deleteCaptureUsecase,
                                                              SetTitleUsecase setTitleUsecase,
                                                              SearchUsecase searchUsecase,
                                                              GetProfilePictureUsecase getProfilePictureUsecase,
                                                              GetEntryPicUsecase getEntryPicUsecase,
                                                              GetUserUsecase getUserUsecase,
                                                              FollowUsecase followUsecase,
                                                              UnfollowUsecase unfollowUsecase) {
        return new JournalPresenterOther(getJournalsUsecase,
                deleteCaptureUsecase,
                setTitleUsecase,
                searchUsecase,
                getProfilePictureUsecase,
                getEntryPicUsecase,
                getUserUsecase,
                followUsecase,
                unfollowUsecase);
    }
}
