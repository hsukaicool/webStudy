import { useState } from 'react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

export default function Profile() {
    const [user, setUser] = useState(null);

    return (
        <div className="page-profile">
            <Navbar variant="profile" />
            <main className="profile-main">
                <h1>Profile</h1>
            </main>
            <Footer variant="profile" />
        </div>
    );
}