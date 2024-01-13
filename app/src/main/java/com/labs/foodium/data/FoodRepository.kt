package com.labs.foodium.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class FoodRepository @Inject constructor(remoteDataSource: RemoteDataSource, localDataStore: LocalDataStore) {

    val remote = remoteDataSource
    val local = localDataStore
}