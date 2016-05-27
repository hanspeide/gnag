/**
 * Copyright 2016 Bryan Kelly
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.btkelly.gnag.api;

import com.btkelly.gnag.extensions.GitHubExtension;
import com.btkelly.gnag.models.*;
import com.btkelly.gnag.utils.diffparser.DiffParserConverterFactory;
import com.btkelly.gnag.utils.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

/**
 * Created by bobbake4 on 12/1/15.
 */
public class GitHubApi {

    public enum Status {
        OK,
        FAIL
    }

    private final GitHubApiClient gitHubApiClient;
    private final GitHubExtension gitHubExtension;

    public GitHubApi(final GitHubExtension gitHubExtension) {
        this.gitHubExtension = gitHubExtension;

        HttpLoggingInterceptor.Logger logger = System.out::println;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(gitHubExtension))
                .addInterceptor(new HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.NONE))
                .build();

        String baseUrl = "https://api.github.com/repos/" + gitHubExtension.getRepoName() + "/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(DiffParserConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubApiClient = retrofit.create(GitHubApiClient.class);
    }

    public Status postGitHubIssueComment(String comment) {

        try {
            Response<GitHubIssueComment> gitHubIssueCommentResponse = gitHubApiClient.postComment(new GitHubIssueComment(comment), gitHubExtension.getIssueNumber()).execute();
            return gitHubIssueCommentResponse.isSuccessful() ? Status.OK : Status.FAIL;
        } catch (IOException ignored) {
            return Status.FAIL;
        }
    }

    public Status postUpdatedGitHubStatus(GitHubStatusType gitHubStatusType, String sha) {

        try {
            Response<GitHubStatus> gitHubStatusResponse = gitHubApiClient.postUpdatedStatus(new GitHubStatus(gitHubStatusType), sha).execute();
            return gitHubStatusResponse.isSuccessful() ? Status.OK : Status.FAIL;
        } catch (IOException ignored) {
            return Status.FAIL;
        }
    }

    public GitHubPRDetails getPRDetails() {

        try {
            Response<GitHubPRDetails> gitHubPRResponse = gitHubApiClient.getPRDetails(gitHubExtension.getIssueNumber()).execute();
            return gitHubPRResponse.body();
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    public GitHubPRDiffWrapper getPRDiffWrapper() {
        try {
            final Response<GitHubPRDiffWrapper> gitHubPullRequestDiffWrapperResponse
                    = gitHubApiClient.getPRDiffWrapper(gitHubExtension.getIssueNumber()).execute();
            
            return gitHubPullRequestDiffWrapperResponse.body();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Status postGitHubPRComment(
            @NotNull final String body,
            @NotNull final String prSha,
            @NotNull final String relativeFilePath,
            final int diffLineIndex) {

        try {
            Response<Void> gitHubPRCommentResponse
                    = gitHubApiClient.postComment(
                            new GitHubPRComment(body, prSha, relativeFilePath, diffLineIndex),
                            gitHubExtension.getIssueNumber()).execute();
            
            return gitHubPRCommentResponse.isSuccessful() ? Status.OK : Status.FAIL;
        } catch (IOException ignored) {
            return Status.FAIL;
        }
    }

}
