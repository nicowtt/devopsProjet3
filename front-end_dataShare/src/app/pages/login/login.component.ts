import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { LoginDTO } from '../../core/models/user.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  get email() { return this.loginForm.get('email')!; }
  get password() { return this.loginForm.get('password')!; }

  onEmailInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.email.setValue(input.value.toLowerCase(), { emitEvent: false });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    const loginDTO: LoginDTO = {
      email: this.email.value,
      password: this.password.value
    };
    this.userService.login(loginDTO).subscribe({
      next: (token) => {
        this.authService.saveToken(token);
        this.toastr.success('Connexion réussie !');
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.toastr.error(err.error?.message || 'Email ou mot de passe incorrect.');
      }
    });
  }
}
