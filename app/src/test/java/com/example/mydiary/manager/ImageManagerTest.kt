package com.example.mydiary.utils.manager

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder


class ImageManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val mockContext = mockk<Context>()
    private val mockUri = mockk<Uri>()
    private lateinit var imageManager: ImageManager

    @Before
    fun setup() {
        // Clear all previous mocks
        clearAllMocks()

        // Mock context package name
        every { mockContext.packageName } returns "com.example.mydiary"

        // Mock context cache directory
        every { mockContext.cacheDir } returns tempFolder.root

        // Mock FileProvider.getUriForFile
        mockkStatic(FileProvider::class)
        every {
            FileProvider.getUriForFile(
                mockContext,
                "com.example.mydiary.provider",
                any()
            )
        } returns mockUri

        imageManager = ImageManager(mockContext)
    }

    @Test
    fun `createTempImageUri creates and returns valid Uri`() {
        // When
        val result = imageManager.createTempImageUri()

        // Then
        assertNotNull(result)
        assertEquals(mockUri, result)

        // Verify temp file was created
        val files = tempFolder.root.listFiles()
        assertNotNull(files)
        assert(files!!.any { it.name.startsWith("tmp_image_file") && it.name.endsWith(".png") })

        // Verify FileProvider was called
        verify {
            FileProvider.getUriForFile(
                mockContext,
                "com.example.mydiary.provider",
                any()
            )
        }
    }

    @Test
    fun `getTempImageUri returns null before createTempImageUri is called`() {
        // When
        val result = imageManager.getTempImageUri()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `getTempImageUri returns correct Uri after createTempImageUri is called`() {
        // Given
        val createdUri = imageManager.createTempImageUri()

        // When
        val result = imageManager.getTempImageUri()

        // Then
        assertEquals(createdUri, result)
        assertEquals(mockUri, result)

        // Verify FileProvider was called exactly once
        verify(exactly = 1) {
            FileProvider.getUriForFile(
                mockContext,
                "com.example.mydiary.provider",
                any()
            )
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}