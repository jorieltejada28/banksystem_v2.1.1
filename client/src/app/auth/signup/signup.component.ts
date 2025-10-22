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
  firstname: string = '';
  middlename: string = '';
  lastname: string = '';
  suffix: string = '';

  // Address Info
  blkRoom: string = '';
  building: string = '';
  street: string = '';
  barangay: string = '';
  province: string = '';
  zipCode: string = '';

  // Contact Info
  contactNo: string = '';
  telNo: string = '';
  email: string = '';

  // ID Info
  validIdType: string = '';
  validIdNumber: string = '';

  // Error message
  errorMessage: string = '';

  constructor(private http: HttpClient) { }

  // Placeholders for each ID
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
    tin: 'e.g. 123-456-789'
  };

  // Regex validation patterns
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
    tin: '^[0-9]{3}-[0-9]{3}-[0-9]{3}$'
  };

  get selectedPattern(): string {
    return this.validIdType ? this.idPatterns[this.validIdType] : '';
  }

  get placeholderText(): string {
    return this.validIdType
      ? this.idPlaceholders[this.validIdType]
      : 'Select an ID first';
  }

  // Submit method
  onSubmit() {
    this.errorMessage = '';

    // Validate required fields
    if (
      !this.firstname.trim() || !this.lastname.trim() ||
      !this.barangay.trim() || !this.province.trim() ||
      !this.zipCode.trim() || !this.contactNo.trim() ||
      !this.email.trim() || !this.validIdType ||
      !this.validIdNumber.trim()
    ) {
      this.errorMessage = 'Please fill up all required fields.';
      return;
    }

    // Validate ID format
    const pattern = new RegExp(this.selectedPattern);
    if (!pattern.test(this.validIdNumber)) {
      this.errorMessage = `Your ${this.validIdType.replace('_', ' ')} must follow this format: ${this.placeholderText}`;
      return;
    }

    // Payload (same keys as User.java)
    const payload = {
      firstname: this.firstname,
      middlename: this.middlename,
      lastname: this.lastname,
      suffix: this.suffix,
      blkRoom: this.blkRoom,
      building: this.building,
      street: this.street,
      barangay: this.barangay,
      province: this.province,
      zipCode: this.zipCode,
      contactNo: this.contactNo,
      telNo: this.telNo,
      email: this.email,
      validIdType: this.validIdType,
      validIdNumber: this.validIdNumber
    };

    // Call backend API
    this.http.post('http://localhost:8080/api/v3/users/signup', payload).subscribe({
      next: () => {
        this.resetForm();
        Swal.fire({
          toast: true,
          icon: 'success',
          title: 'Registration successful!',
          position: 'top-end',
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true,
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
    this.firstname = '';
    this.middlename = '';
    this.lastname = '';
    this.suffix = '';
    this.blkRoom = '';
    this.building = '';
    this.street = '';
    this.barangay = '';
    this.province = '';
    this.zipCode = '';
    this.contactNo = '';
    this.telNo = '';
    this.email = '';
    this.validIdType = '';
    this.validIdNumber = '';
  }
}
