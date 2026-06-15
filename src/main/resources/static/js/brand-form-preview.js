document.addEventListener("DOMContentLoaded", function () {
    const logoInput = document.getElementById("logo");
    const nameInput = document.getElementById("name");

    const logoPreview = document.getElementById("brandLogoPreview");
    const logoEmpty = document.getElementById("brandLogoEmpty");
    const namePreview = document.getElementById("brandNamePreview");

    const emptyPixel = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";

    if (logoInput && logoPreview && logoEmpty) {
        logoInput.addEventListener("input", function () {
            const url = logoInput.value.trim();

            if (!url) {
                logoPreview.src = emptyPixel;
                logoPreview.classList.add("d-none");
                logoEmpty.classList.remove("d-none");
                return;
            }

            logoPreview.onload = function () {
                logoPreview.classList.remove("d-none");
                logoEmpty.classList.add("d-none");
            };

            logoPreview.onerror = function () {
                logoPreview.classList.add("d-none");
                logoEmpty.classList.remove("d-none");
            };

            logoPreview.src = url;
        });
    }

    if (nameInput && namePreview) {
        nameInput.addEventListener("input", function () {
            const name = nameInput.value.trim();
            namePreview.textContent = name || "Nueva marca";
        });
    }
});