package com.nyahonk.odfreader.domain

import android.net.Uri
import com.nyahonk.odfreader.domain.model.Options
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriInteractor @Inject constructor(
    private val metadataLoader: MetadataLoader,
    private val odfLoader: OdfLoader
) {

    fun loadUri(uri: Uri, persistentUri: Boolean): Single<Uri> {
        return Single.just(
            Options().apply {
                this.originalUri = uri
                this.persistentUri = persistentUri
            }
        ).flatMap { loadWithOptions(it) }
    }

    private fun loadWithOptions(options: Options): Single<Uri> {

        return Single.just(metadataLoader.loadSync(options))
            .map { result -> odfLoader.loadSync(result.options!!) }
            .map { result -> result.partUris[0] }
    }
}