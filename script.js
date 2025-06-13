

function showPage(pageNumber) {
      // Hide all pages first
      let pages = document.querySelectorAll('.page');
      pages.forEach(page => {
        page.style.display = 'none';
      });

      // Show the selected page
      document.getElementById('page' + pageNumber).style.display = 'block';
      // Add the class to indicate a page is open
      document.body.classList.add('page-open');

      // Hide the introduction section when any page is selected
      document.getElementById('intro-section').style.display = 'none';
    }

    // To make sure the page is displayed in full-screen by default
    window.onload = function () {
      let pages = document.querySelectorAll('.page');
      pages.forEach(page => {
        page.style.display = 'none';
      });
    };