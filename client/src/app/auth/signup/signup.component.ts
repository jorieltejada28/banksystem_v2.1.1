import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  // Personal Info
  first_name: string = '';
  middle_name: string = '';
  last_name: string = '';
  suffix: string = '';

  // Address Info
  blk_no_or_room_no: string = '';
  building: string = '';
  street: string = '';
  barangay: string = '';
  province: string = '';
  zip_code: string = '';

  // Contact Info
  contact_no: string = '';
  tel_no: string = '';
  email_address: string = '';

  // ID Info
  selectedId: string = '';
  id_no: string = '';

  // Error message (plain text display)
  errorMessage: string = '';

  constructor(private http: HttpClient) { }

  // Map each ID to its placeholder
  idPlaceholders: { [key: string]: string } = {
    passport: 'e.g. P1234567A',
    driver_license: 'e.g. N01-23-456789',
    umid: 'e.g. CRN-0123-4567890-1',
    sss: 'e.g. 34-5678901-2',
    gsis: 'e.g. 1234567890',
    prc: 'e.g. 1234567',
    voter: 'e.g. 1234-5678-9012-3456',
    postal: 'e.g. 1234-5678-9012',
    national_id: 'e.g. 1234-5678-9012-3456',
    tin: 'e.g. 123-456-789',
  };

  // Regex patterns for each ID type
  idPatterns: { [key: string]: string } = {
    passport: '^[A-Z][0-9]{7}[A-Z]$',
    driver_license: '^[A-Z0-9-]{5,15}$',
    umid: '^CRN-[0-9]{4}-[0-9]{7}-[0-9]$',
    sss: '^[0-9]{2}-[0-9]{7}-[0-9]$',
    gsis: '^[0-9]{10}$',
    prc: '^[0-9]{7}$',
    voter: '^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$',
    postal: '^[0-9]{4}-[0-9]{4}-[0-9]{4}$',
    national_id: '^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$',
    tin: '^[0-9]{3}-[0-9]{3}-[0-9]{3}$',
  };

  get selectedPattern(): string {
    return this.selectedId ? this.idPatterns[this.selectedId] : '';
  }

  get placeholderText(): string {
    return this.selectedId
      ? this.idPlaceholders[this.selectedId]
      : 'Select an ID first';
  }

  // Submit method with validation
  onSubmit() {
    this.errorMessage = '';

    // Validate required fields
    if (!this.first_name.trim() || !this.last_name.trim() ||
      !this.barangay.trim() || !this.province.trim() ||
      !this.zip_code.trim() || !this.contact_no.trim() ||
      !this.email_address.trim() || !this.selectedId ||
      !this.id_no.trim()) {
      this.errorMessage = 'Please fill up all required fields.';
      return;
    }

    // Validate ID format
    const pattern = new RegExp(this.selectedPattern);
    if (!pattern.test(this.id_no)) {
      this.errorMessage = `Your ${this.selectedId.replace('_', ' ')} must follow this format: ${this.placeholderText}`;
      return;
    }

    // Payload
    const payload = {
      firstname: this.first_name,
      middlename: this.middle_name,
      lastname: this.last_name,
      suffix: this.suffix,
      blk_room: this.blk_no_or_room_no,
      building: this.building,
      street: this.street,
      barangay: this.barangay,
      province: this.province,
      zip_code: this.zip_code,
      contact_no: this.contact_no,
      tel_no: this.tel_no,
      email: this.email_address,
      valid_id_type: this.selectedId,
      valid_id_number: this.id_no
    };

    // API call
    this.http.post('http://localhost:8080/api/v3/users', payload).subscribe({
      next: () => {
        this.resetForm();
        Swal.fire({
          toast: true,
          position: 'top-end',
          icon: 'success',
          title: 'Registration successful!',
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true
        });
      },
      error: (err) => {
        if (err.status === 0) {
          this.errorMessage = 'Server is down. Please try again later.';
        } else if (err.status === 404) {
          this.errorMessage = 'API endpoint not found.';
        } else if (err.status === 500) {
          this.errorMessage = 'Internal server error.';
        } else {
          this.errorMessage = 'Failed to submit data.';
        }
        console.error(err);
      }
    });
  }

  private resetForm() {
    this.first_name = '';
    this.middle_name = '';
    this.last_name = '';
    this.suffix = '';
    this.blk_no_or_room_no = '';
    this.building = '';
    this.street = '';
    this.barangay = '';
    this.province = '';
    this.zip_code = '';
    this.contact_no = '';
    this.tel_no = '';
    this.email_address = '';
    this.selectedId = '';
    this.id_no = '';
  }
}
