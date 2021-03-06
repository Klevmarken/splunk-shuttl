// Copyright (C) 2011 Splunk Inc.
//
// Splunk Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.splunk.shuttl.archiver.bucketsize;

import static com.splunk.shuttl.testutil.TUtilsFile.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.splunk.shuttl.archiver.archive.PathResolver;
import com.splunk.shuttl.archiver.filesystem.ArchiveFileSystem;
import com.splunk.shuttl.archiver.model.Bucket;
import com.splunk.shuttl.testutil.TUtilsBucket;

@Test(groups = { "fast-unit" })
public class ArchiveBucketSizeTest {

	private ArchiveBucketSize archiveBucketSize;
	private PathResolver pathResolver;
	private ArchiveFileSystem archiveFileSystem;
	private BucketSizeIO bucketSizeIO;

	@BeforeMethod
	public void setUp() {
		pathResolver = mock(PathResolver.class);
		archiveFileSystem = mock(ArchiveFileSystem.class);
		bucketSizeIO = mock(BucketSizeIO.class);
		archiveBucketSize = new ArchiveBucketSize(pathResolver, bucketSizeIO,
				archiveFileSystem);
	}

	public void putSize_givenBucketSizeFile_getsFileWithBucketSize() {
		Bucket bucket = mock(Bucket.class);
		archiveBucketSize.putSize(bucket);
		verify(bucketSizeIO).getFileWithBucketSize(bucket);
	}

	public void putSize_givenPathResolver_getsMetadataFolderForBucket() {
		Bucket bucket = mock(Bucket.class);
		archiveBucketSize.putSize(bucket);
		verify(pathResolver).getBucketSizeFileUriForBucket(bucket);
	}

	public void putSize_givenFileWithBucketSizeAndPathOnArchiveFileSystem_transfersFileWithSizeToArchiveFileSystem()
			throws IOException {
		Bucket bucket = mock(Bucket.class);
		File fileWithBucketsSize = createFile();
		when(bucketSizeIO.getFileWithBucketSize(bucket)).thenReturn(
				fileWithBucketsSize);
		URI pathOnArchiveFileSystem = URI.create("path:/on/archive/file/system");
		when(pathResolver.getBucketSizeFileUriForBucket(bucket)).thenReturn(
				pathOnArchiveFileSystem);
		archiveBucketSize.putSize(bucket);
		verify(archiveFileSystem).putFileAtomically(fileWithBucketsSize,
				pathOnArchiveFileSystem);
	}

	public void getSize_givenUriToFileWithBucketSize_passesUriToBucketSizeFileForReading() {
		Bucket remoteBucket = TUtilsBucket.createRemoteBucket();
		URI uriToFileWIthBucketSize = URI.create("remote:/uri");
		when(pathResolver.getBucketSizeFileUriForBucket(remoteBucket)).thenReturn(
				uriToFileWIthBucketSize);
		archiveBucketSize.getSize(remoteBucket);
		verify(bucketSizeIO).readSizeFromRemoteFile(uriToFileWIthBucketSize);
	}

	public void getSize_givenBucketFileSizeReadSuccessfully_returnValue() {
		long size = 4711;
		when(bucketSizeIO.readSizeFromRemoteFile(any(URI.class)))
				.thenReturn(size);
		long actualSize = archiveBucketSize.getSize(TUtilsBucket
				.createRemoteBucket());
		assertEquals(size, actualSize);

	}
}
