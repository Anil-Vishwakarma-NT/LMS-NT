// YouTube IFrame API Ready Callback
function onYouTubeIframeAPIReady() {
    console.log("YouTube IFrame API is ready!");
    // Now it's safe to create YT.Player instances
}


//pdf library
const pdfjsLib = window['pdfjs-dist/build/pdf'];
pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.worker.min.js';


// Fetch Course Content by ID
document.getElementById("fetchContent").addEventListener("click", function () {
    const courseId = document.getElementById("courseId").value;

    if (!courseId) {
        alert("Please enter a valid course ID!");
        return;
    }

    const apiUrl = `http://localhost:8080/api/course-content/courseid/${courseId}`;
    fetchData(apiUrl)
        .then(data => {
            console.log("Fetched Course Content Data:", data);
            renderTable(data, "contentTable");
        })
        .catch(error => {
            console.error("Error fetching data:", error);
            alert("Unable to fetch course content. Please check the Course ID.");
        });
});


// Fetch All Course Contents
document.getElementById("fetchAllContent").addEventListener("click", function () {
    const apiUrl = "http://localhost:8080/api/course-content";
    fetchData(apiUrl)
        .then(data => {
            console.log("Fetched All Course Content Data:", data);
            renderTable(data, "contentTable");
        })
        .catch(error => {
            console.error("Error fetching data:", error);
            alert("Unable to fetch course content. Please try again later.");
        });
});


// Fetch Data from API
function fetchData(apiUrl) {
    console.log(`Making GET request to: ${apiUrl}`);
    return fetch(apiUrl).then(response => {
        if (!response.ok) {
            throw new Error("Failed to fetch data");
        }
        return response.json();
    });
}


// Render Table with Course Content
function renderTable(data, tableBodyId) {
    const tableBody = document.getElementById(tableBodyId);
    tableBody.innerHTML = ""; // Clear previous content

    data.forEach(item => {
        const row = `
            <tr>
                <td>${item.courseId || ""}</td>
                <td>${item.title}</td>
                <td>${item.description}</td>
                <td>
                    ${item.videoLink.includes(".pdf")
                        ? `<button onclick="renderPdfViewer('${item.videoLink}', ${item.courseId}, ${item.courseContentId})">View PDF</button>`
                        : `<button onclick="renderMediaPlayer('${item.videoLink}', ${item.courseId}, ${item.courseContentId})">Watch Video</button>`}
                </td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });

    console.log("Rendered Table Content:", data);
}

// Render Media Player
function renderMediaPlayer(videoLink, courseId, courseContentId) {
    const mediaPlayer = document.getElementById("mediaPlayer");
    const videoModal = document.getElementById("videoModal");

    mediaPlayer.innerHTML = ""; // Clear existing content
    videoModal.setAttribute("data-course-id", courseId);
    videoModal.setAttribute("data-course-content-id", courseContentId);

    let formattedVideoPath = videoLink.match(/\.(mp4|webm|mov)$/i)  // ✅ If it's just a filename, format it
        ? `http://localhost:8080/videos/${videoLink}`
        : videoLink; // Otherwise, use as-is

    if (videoLink.includes("youtube.com") || videoLink.includes("youtu.be")) {
        renderYouTubePlayer(videoLink, courseId, courseContentId);
    }
    else if (videoLink.match(/\.(mp4|webm|mov)$/i)) {  // ✅ Detect local file formats
        const playerHtml = `<video id="videoPlayer" controls>
                                <source src="${formattedVideoPath}" type="video/mp4">
                                Your browser does not support the video tag.
                            </video>`;
        mediaPlayer.innerHTML = playerHtml;

        videoModal.style.display = "flex"; // Show modal

        // ✅ Resume playback from last watched time
        const videoElement = document.getElementById("videoPlayer");
        fetchLocalVideoProgress(courseId, courseContentId, videoElement);
    } else {
        console.error("Unsupported video format:", videoLink);
    }
}


//function to fetch local video progress and set resume
function fetchLocalVideoProgress(courseId, courseContentId, videoElement) {
    if (!courseId || !courseContentId) {
        console.warn("Invalid Course ID or Content ID. Skipping progress fetch.");
        videoElement.currentTime = 0; // Start from beginning
        return Promise.resolve();
    }

    const apiUrl = `http://localhost:8080/api/video-progress/${courseId}/${courseContentId}`;
    console.log(`Fetching local video progress from API: ${apiUrl}`);

    return fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    console.warn("No progress found. Starting from the beginning.");
                    videoElement.currentTime = 0; // Default to start
                    window.storedProgress = { lastWatchedTime: 0, percentageCompleted: 0 };
                    return { lastWatchedTime: 0, percentageCompleted: 0 };
                }
                throw new Error("Failed to fetch video progress.");
            }
            return response.json();
        })
        .then(data => {
            console.log("Fetched Local Video Progress Data:", data);

            const lastWatchedTime = data.lastWatchedTime || 0;
            videoElement.currentTime = lastWatchedTime; // ✅ Resume from last watched time
            console.log(`Resuming playback from: ${lastWatchedTime}s`);

            videoElement.addEventListener("ended", () => {
                console.log("Local video ended! Waiting 5 seconds before restarting...");

                setTimeout(() => {
                    console.log("Restarting local video now...");
                    videoElement.currentTime = 0; // ✅ Reset to start
                    videoElement.play(); // ✅ Restart playback
                }, 5000); // ✅ 5-second delay
            });

            // Store fetched progress globally for later use
            window.storedProgress = {
                lastWatchedTime: lastWatchedTime,
                percentageCompleted: data.percentageCompleted || 0
            };
            console.log("Updated storedProgress:", window.storedProgress);
        })
        .catch(error => {
            console.error("Error fetching progress:", error);
            videoElement.currentTime = 0; // Fail-safe: Start from 0
        });
}


//const videoElement = document.getElementById("videoPlayer");
//
//videoElement.addEventListener("timeupdate", () => {
//    const currentTime = videoElement.currentTime;
//    const duration = videoElement.duration;
//    let percentageCompleted = Math.floor((currentTime / duration) * 100);
//
//    console.log(`Tracking Local Video Progress: ${currentTime}s (${percentageCompleted}%)`);
//
//    // ✅ Prevent downgrade by comparing with stored progress
//    if (percentageCompleted < window.storedProgress.percentageCompleted) {
//        console.log(`Ignoring lower progress (${percentageCompleted}%), using stored value (${window.storedProgress.percentageCompleted}%)`);
//        percentageCompleted = window.storedProgress.percentageCompleted;
//    }
//
//    // ✅ Periodically send progress updates to backend
//    updateVideoProgress(videoModal.getAttribute("data-course-id"), videoModal.getAttribute("data-course-content-id"), currentTime, percentageCompleted, percentageCompleted);
//});



//function to render youtube player
let playerInstance = null; // Global reference for YouTube Player instance
function renderYouTubePlayer(videoLink, courseId, courseContentId) {
    const mediaPlayer = document.getElementById("mediaPlayer");
    const videoModal = document.getElementById("videoModal");

    // Sanitize Video ID
    const videoId = videoLink.includes("v=")
        ? videoLink.split("v=")[1]?.split("&")[0]
        : videoLink.split("/").pop().split("?")[0];
    console.log(`Sanitized YouTube Video ID: ${videoId}`);

    // Clear existing player and show modal
    mediaPlayer.innerHTML = `<div id="youtubePlayer"></div>`;
    videoModal.style.display = "flex";

    // Initialize YouTube Player
    playerInstance = new YT.Player("youtubePlayer", {
        videoId: videoId,
        events: {
            "onReady": function () {
                console.log("YouTube Player is ready. Fetching progress...");
                fetchVideoProgress(courseId, courseContentId, playerInstance);
            },
            "onStateChange": function (event) {
                console.log("YouTube Player state changed:", event.data);

            if (event.data === YT.PlayerState.ENDED) {
                console.log("YouTube video ended! Waiting 5 seconds before restarting...");

                setTimeout(() => {
                    console.log("Restarting YouTube video now...");
                    event.target.seekTo(0); // ✅ Reset to start
                    event.target.playVideo(); // ✅ Restart playback
                }, 5000); // ✅ 5-second delay
            }

            }
        }
    });

    console.log("YouTube Player successfully initialized for video ID:", videoId);
}


// Function to Fetch Youtube Video Progress and Resume Playback
function fetchVideoProgress(courseId, courseContentId, playerInstance) {
    if (!courseId || !courseContentId) {
        console.warn("Invalid Course ID or Content ID. Skipping progress fetch.");
        playerInstance.seekTo(0, true); // Start from the beginning
        return Promise.resolve();
    }

    const apiUrl = `http://localhost:8080/api/video-progress/${courseId}/${courseContentId}`;
    console.log(`Fetching video progress from API: ${apiUrl}`);

    return fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    console.warn(`No progress found. Starting from the beginning.`);
                    playerInstance.seekTo(0, true); // Start at 0 if no progress
                    window.storedProgress = { lastWatchedTime: 0, percentageCompleted: 0 };
                    return { lastWatchedTime: 0, percentageCompleted: 0 };
                }
                throw new Error("Failed to fetch video progress.");
            }
            return response.json();
        })
        .then(data => {
            console.log("Fetched Video Progress Data:", data);

            const lastWatchedTime = data.lastWatchedTime || 0;
            playerInstance.seekTo(lastWatchedTime, true); // Resume from last watched time
            console.log(`Resuming playback from: ${lastWatchedTime}s`);

            // Store fetched progress globally for later use
            window.storedProgress = {
                lastWatchedTime: lastWatchedTime,
                percentageCompleted: data.percentageCompleted || 0
            };
            console.log("Updated storedProgress:", window.storedProgress);
        })
        .catch(error => {
            console.error("Error fetching progress:", error);
            playerInstance.seekTo(0, true); // Fail-safe: Start from 0
        });
}

// Close Video Modal
document.getElementById("closeModal").addEventListener("click", function () {
    const videoModal = document.getElementById("videoModal");

    const courseId = videoModal.getAttribute("data-course-id");
    const courseContentId = videoModal.getAttribute("data-course-content-id");
    const videoElement = document.getElementById("videoPlayer"); // ✅ Get local video player

    let currentTime = 0, duration = 0, percentageCompleted = 0;

    if (playerInstance) {
        // ✅ YouTube Player handling
        currentTime = playerInstance.getCurrentTime() || 0;
        duration = playerInstance.getDuration() || 0;
    } else if (videoElement) {
        // ✅ Local Video handling
        currentTime = videoElement.currentTime || 0;
        duration = videoElement.duration || 0;
    } else {
        console.warn("No valid player found. Skipping progress update.");
        return;
    }

    if (duration === 0) {
        console.warn("Video duration unavailable. Skipping progress update.");
        return;
    }

    const lastWatchedTime = Math.floor(currentTime);
    percentageCompleted = Math.floor((currentTime / duration) * 100);

    const threshold = 1; // Allow a 1-second tolerance
    if (lastWatchedTime >= (duration - threshold)) {
        percentageCompleted = 100; // ✅ Consider fully completed
        console.log(`Near the end of the video, marking as 100% completed.`);
    }

    if (percentageCompleted < storedProgress.percentageCompleted) {
        console.log(`Using stored progress (${storedProgress.percentageCompleted}%) instead of lower value (${percentageCompleted}%).`);
        percentageCompleted = storedProgress.percentageCompleted;
    }

    console.log(`Final percentageCompleted: ${percentageCompleted}. Updating progress...`);

    updateVideoProgress(courseId, courseContentId, lastWatchedTime, percentageCompleted, percentageCompleted);

    // ✅ Close modal & cleanup
    videoModal.style.display = "none";
    document.getElementById("mediaPlayer").innerHTML = "";
    playerInstance = null;
});

// Update Video Progress in Backend
function updateVideoProgress(courseId, courseContentId, lastWatchedTime, percentageCompleted, courseTotalProgress) {
    const apiUrl = "http://localhost:8080/api/video-progress/update";

    const progressData = {
        courseId: parseInt(courseId, 10) || null,
        courseContentId: parseInt(courseContentId, 10) || null,
        lastWatchedTime: parseFloat(lastWatchedTime) || 0.0,
        percentageCompleted: parseFloat(percentageCompleted) || 0.0,
        courseTotalProgress: parseFloat(courseTotalProgress) || 0.0
    };

    console.log("Sending progress data to backend:", JSON.stringify(progressData, null, 2));
    console.log("Sending this payload:", JSON.stringify(progressData, null, 2));
    fetch(apiUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(progressData)
    })
        .then(response => {
            if (!response.ok) {
                console.error("Failed to update progress.");
                throw new Error("Progress update failed");
            }
            return response.json();
        })
        .then(data => {
            console.log("Progress updated successfully:", data);

            // Update local stored progress after successful backend update
            window.storedProgress = {
                lastWatchedTime: progressData.lastWatchedTime,
                percentageCompleted: progressData.percentageCompleted
            };
            console.log("Stored progress updated after backend sync:", window.storedProgress);
        })
        .catch(error => {
            console.error("Error updating progress:", error);
        });
}


//PDF Reader
async function renderPdfViewer(pdfLink, courseId, courseContentId) {
    const pdfViewerModal = document.getElementById("pdfViewerModal");
    const screenBlocker = document.getElementById("screenBlocker");
    const pdfCanvasContainer = document.getElementById("pdfCanvasContainer");
    const closeButton = document.getElementById("closeButton");
    const timerMessage = document.getElementById("timerMessage");

    if (!closeButton) {
        console.error("Close button not found!");
        return;
    }

    // ✅ Reset close button settings before opening new PDF
    closeButton.disabled = true;
    closeButton.style.cursor = "not-allowed";
    closeButton.textContent = "Close Viewer";

    // ✅ Reset timer display message
    timerMessage.textContent = "You can close this after 10 seconds.";

    // ✅ Reset countdown logic
    let timeLeft = 10;
    clearInterval(window.timerInterval); // Clears previous interval if any

    let timerRunning = true; // ✅ Track whether timer is active

    window.timerInterval = setInterval(() => {
    if (timerRunning) {
        timeLeft--;
    }
        timerMessage.textContent = `You can close this after ${timeLeft} seconds.`;

        if (timeLeft <= 0) {
            clearInterval(window.timerInterval);
            closeButton.disabled = false;
            closeButton.style.cursor = "pointer";
            closeButton.textContent = "Close Viewer";
        }
    }, 1000); // Updates every second

    document.addEventListener("visibilitychange", () => {
        if (document.hidden) {
            console.log("Tab switched! Timer paused.");
            timerRunning = false;
        } else {
            console.log("Tab active again! Timer resumed.");
            timerRunning = true;
        }
    });

    // Fetch previous progress before opening viewer
    await fetchPdfProgress(courseId, courseContentId);

    pdfCanvasContainer.innerHTML = ""; // Clear previous content
    pdfViewerModal.style.display = "block";
    screenBlocker.style.display = "block";

    try {
        // Load new PDF
        const PDFLink = `${window.location.origin}/pdf/${pdfLink}`;
        const loadingTask = pdfjsLib.getDocument(PDFLink);
        const pdfDoc = await loadingTask.promise;

        if (!pdfDoc || !pdfDoc.numPages) {
            throw new Error("PDF failed to load or is empty.");
        }

        console.log("PDF Loaded Successfully:", pdfDoc);

        let totalPages = pdfDoc.numPages;
        let lastRecordedPage = window.storedPdfProgress.currentPage || 1;

        if (lastRecordedPage == totalPages) {
            console.log("User was on the last page! Resetting to start...");
            lastRecordedPage = 1; // ✅ Reset progress to start
        }

        async function renderPage(pageNum, pdfDoc) {
            const page = await pdfDoc.getPage(pageNum);
            const viewport = page.getViewport({ scale: 1.5 });

            const canvas = document.createElement("canvas");
            canvas.setAttribute("data-page-number", pageNum); // ✅ Assign Page Number
            const ctx = canvas.getContext("2d");

            canvas.height = viewport.height;
            canvas.width = viewport.width;

            const renderContext = { canvasContext: ctx, viewport: viewport };
            await page.render(renderContext);

            pdfCanvasContainer.appendChild(canvas);
        }

        for (let pageNum = 1; pageNum <= totalPages; pageNum++) {
            await renderPage(pageNum, pdfDoc);
        }

        setTimeout(() => {
            const targetPageElement = document.querySelector(`[data-page-number="${lastRecordedPage}"]`);
            if (targetPageElement) {
                targetPageElement.scrollIntoView({ behavior: "smooth", block: "start" });
                console.log(`Scrolling to Page ${lastRecordedPage}`);
            } else {
                console.warn(`Failed to locate page ${lastRecordedPage}. Scrolling skipped.`);
            }
        }, 1000); // Extra time to ensure rendering is complete

        // ✅ Prevent duplicate API calls by ensuring listener is only added once
        pdfCanvasContainer.removeEventListener("scroll", trackPdfProgress); // Remove previous listeners if any
        pdfCanvasContainer.addEventListener("scroll", trackPdfProgress);

        function trackPdfProgress() {
            getCurrentPage(pdfCanvasContainer, pdfDoc).then(({ currentPage, completionPercentage }) => {
                if (currentPage !== lastRecordedPage) {
                    lastRecordedPage = currentPage;
                    console.log(`Tracking Progress: Page ${currentPage}, Completion ${completionPercentage}%`);
                }
            });
        }

        // ✅ Close button functionality (ensures proper stored progress usage)
        closeButton.addEventListener("click", async function handleCloseClick() {
        let { currentPage, completionPercentage } = await getCurrentPage(pdfCanvasContainer, pdfDoc);

        if (completionPercentage < window.storedPdfProgress.percentageCompleted) {
            completionPercentage = window.storedPdfProgress.percentageCompleted;
        }

        console.log(`Final PDF completion percentage: ${completionPercentage}. Updating progress...`);
        updateVideoProgress(courseId, courseContentId, currentPage, completionPercentage, completionPercentage);

        pdfViewerModal.style.display = "none";
        screenBlocker.style.display = "none";
        console.log("Closing PDF Viewer.");

        // ✅ Remove event listener after execution to prevent duplicate calls
        closeButton.removeEventListener("click", handleCloseClick);
        });

    } catch (error) {
        console.error("Error loading PDF:", error);
    }
}

// Render a single page
async function renderPage(pageNum, pdfDoc) {
    const page = await pdfDoc.getPage(pageNum);
    const viewport = page.getViewport({ scale: 1.5 });

    const canvas = document.createElement("canvas");
    const context = canvas.getContext("2d");

    canvas.height = viewport.height;
    canvas.width = viewport.width;
    document.getElementById("pdfCanvasContainer").appendChild(canvas);

    await page.render({ canvasContext: context, viewport }).promise;
}

// Detect current visible page
async function getCurrentPage(pdfCanvasContainer, pdfDoc) {
    try {
        const totalPages = pdfDoc.numPages;
        let currentPage = 1;
        const containers = pdfCanvasContainer.querySelectorAll("canvas");

        let minDifference = Infinity;
        let closestPage = 1;

        containers.forEach((canvas, index) => {
            const rect = canvas.getBoundingClientRect();
            const midPoint = (rect.top + rect.bottom) / 2; // Find middle of the canvas

            const diff = Math.abs(midPoint - window.innerHeight / 2); // Compare to viewport center

            if (diff < minDifference) {
                minDifference = diff;
                closestPage = index + 1;
            }
        });

        currentPage = closestPage;

        let completionPercentage = Math.floor((currentPage / totalPages) * 100);
        if (currentPage === totalPages) {
            completionPercentage = 100; // ✅ Ensure last page sends 100%
        }

        console.log(`Detected Page: ${currentPage}, Completion: ${completionPercentage}%`);
        return { currentPage, completionPercentage };
    } catch (error) {
        console.error("Error detecting current page via PDF.js:", error);
        return { currentPage: 1, completionPercentage: 0 };
    }
}


//function to fetch pdf progress
function fetchPdfProgress(courseId, courseContentId) {
    if (!courseId || !courseContentId) {
        console.warn("Invalid Course ID or Content ID. Skipping progress fetch.");
        window.storedPdfProgress = { currentPage: 1, percentageCompleted: 0 }; // Start from 0 if no data
        return Promise.resolve();
    }

    const apiUrl = `http://localhost:8080/api/video-progress/${courseId}/${courseContentId}`;
    console.log(`Fetching PDF progress from API: ${apiUrl}`);

    return fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    console.warn(`No PDF progress found. Starting from the beginning.`);
                    window.storedPdfProgress = { currentPage:1, percentageCompleted: 0 };
                    return { percentageCompleted: 0 };
                }
                throw new Error("Failed to fetch PDF progress.");
            }
            return response.json();
        })
        .then(data => {
            console.log("Fetched PDF Progress Data:", data);

            // Store fetched PDF progress globally for later use
            window.storedPdfProgress = {
                currentPage : data.lastWatchedTime || 1,
                percentageCompleted: data.percentageCompleted || 0
            };
            console.log("Updated storedPdfProgress:", window.storedPdfProgress);
        })
        .catch(error => {
            console.error("Error fetching PDF progress:", error);
            window.storedPdfProgress = { currentPage:1, percentageCompleted: 0 }; // Fail-safe: Start from 0
        });
}



