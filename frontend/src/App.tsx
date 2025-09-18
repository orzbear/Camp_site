import React from 'react'
import { Routes, Route } from 'react-router-dom'
import { Search } from './pages/Search'
import { Spot } from './pages/Spot'
import { Planner } from './pages/Planner'
import { Alerts } from './pages/Alerts'
import { Layout } from './components/Layout'

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Search />} />
        <Route path="/spot/:id" element={<Spot />} />
        <Route path="/planner" element={<Planner />} />
        <Route path="/alerts" element={<Alerts />} />
      </Routes>
    </Layout>
  )
}

export default App
