import React, { useEffect, useMemo, useState } from 'react';
import { FiAlertTriangle, FiChevronDown, FiSend } from 'react-icons/fi';
import { getReportsByUser, createReport, getBookingsByCustomer } from '../../../../services/api';

export default function ReportsTab({ user }) {
  const [reports, setReports] = useState([]);
  const [reason, setReason] = useState('');
  const [targetType, setTargetType] = useState('PROVIDER');
  const [targetId, setTargetId] = useState('');
  const [targetTypeDropdownOpen, setTargetTypeDropdownOpen] = useState(false);
  const [targetDropdownOpen, setTargetDropdownOpen] = useState(false);

  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user?.id) return;

    fetchReports(user.id);
    fetchAllOptions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user]);

  const bookedProviders = useMemo(() => {
    const providerMap = new Map();

    bookings.forEach((booking) => {
      const provider = booking.provider;
      if (provider?.id && !providerMap.has(provider.id)) {
        providerMap.set(provider.id, provider);
      }
    });

    return Array.from(providerMap.values());
  }, [bookings]);

  const fetchReports = async (id) => {
    try {
      const res = await getReportsByUser(id);
      setReports(res.data || []);
    } catch (err) {
      console.error('Error fetching reports:', err);
    }
  };

  const fetchAllOptions = async () => {
    try {
      const bookingRes = await getBookingsByCustomer(user.id);
      setBookings(bookingRes.data || []);
    } catch (err) {
      console.error('Error fetching reportable bookings:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!user?.id) return alert('User not loaded yet.');
    if (!targetId) return alert('Please select a valid target.');

    try {
      await createReport(user.id, targetType, targetId, reason);
      alert('✅ Report submitted successfully!');
      setReason('');
      setTargetId('');
      fetchReports(user.id);
    } catch (err) {
      console.error('Error creating report:', err);
      alert(err.response?.data?.message || '❌ Failed to submit report.');
    }
  };

  const targetTypeOptions = [
    { value: 'PROVIDER', label: 'Provider' },
    { value: 'BOOKING', label: 'Booking' },
  ];
  const selectedTargetType = targetTypeOptions.find((option) => option.value === targetType);
  const targetOptions = targetType === 'PROVIDER' ? bookedProviders : bookings;
  const selectedTarget = targetOptions.find((option) => String(option.id) === targetId);

  const getTargetLabel = (option) => {
    if (targetType === 'PROVIDER') {
      return option.name || option.fullName || option.email || 'Provider';
    }

    const serviceCategory = option.service?.category || 'Unnamed Category';
    const serviceSubcategory = option.service?.subcategory || 'Unnamed Subcategory';
    const providerName = option.provider?.name || option.provider?.fullName || option.service?.providerName || 'Unknown Provider';
    const date = option.bookingDate ? new Date(option.bookingDate).toLocaleDateString() : 'Unknown Date';
    const status = option.status || 'Pending';

    return `${date} - ${providerName} - ${serviceCategory} (${serviceSubcategory}) - ${status}`;
  };

  const getEmptyTargetMessage = () => {
    if (loading) return 'Loading...';
    return targetType === 'PROVIDER' ? 'No booked providers found' : 'No bookings found';
  };

  return (
    <div className="grid gap-6 xl:grid-cols-[1.05fr_0.95fr]">
      <DashboardSection title="Reports" subtitle="Submit a report and track your own report history">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <span className="mb-2 block text-sm font-medium text-slate-600">Target Type</span>
              <div className="relative">
                <button
                  type="button"
                  onClick={() => {
                    setTargetDropdownOpen(false);
                    setTargetTypeDropdownOpen((current) => !current);
                  }}
                  className="flex w-full items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-left text-sm text-slate-700 transition hover:border-slate-300 focus:border-[#1d4ed8] focus:outline-none"
                >
                  <span className="min-w-0 flex-1 truncate text-slate-900">{selectedTargetType?.label || 'Select type'}</span>
                  <FiChevronDown className={`shrink-0 text-slate-400 transition ${targetTypeDropdownOpen ? 'rotate-180' : ''}`} />
                </button>

                {targetTypeDropdownOpen && (
                  <div className="absolute left-0 right-0 top-full z-40 mt-2 rounded-2xl border border-slate-200 bg-white p-2 shadow-xl">
                    <div className="space-y-2">
                      {targetTypeOptions.map((option) => {
                        const isSelected = targetType === option.value;

                        return (
                          <button
                            key={option.value}
                            type="button"
                            onClick={() => {
                              setTargetType(option.value);
                              setTargetId('');
                              setTargetTypeDropdownOpen(false);
                            }}
                            className={`flex w-full items-center justify-between gap-3 rounded-xl px-3 py-2.5 text-left text-sm transition ${
                              isSelected
                                ? 'bg-[#1d4ed8] text-white shadow-sm'
                                : 'bg-slate-50 text-slate-700 hover:bg-slate-100'
                            }`}
                          >
                            <span className="min-w-0 flex-1 truncate">{option.label}</span>
                            {isSelected && <span className="shrink-0 text-xs font-semibold uppercase tracking-[0.16em]">Selected</span>}
                          </button>
                        );
                      })}
                    </div>
                  </div>
                )}
              </div>
            </div>

            <div>
              <span className="mb-2 block text-sm font-medium text-slate-600">Target</span>
              <div className="relative">
                <button
                  type="button"
                  onClick={() => {
                    setTargetTypeDropdownOpen(false);
                    setTargetDropdownOpen((current) => !current);
                  }}
                  title={selectedTarget ? getTargetLabel(selectedTarget) : 'Select target'}
                  className="flex w-full items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-left text-sm text-slate-700 transition hover:border-slate-300 focus:border-[#1d4ed8] focus:outline-none"
                >
                  <span className={`min-w-0 flex-1 truncate ${selectedTarget ? 'text-slate-900' : 'text-slate-400'}`}>
                    {selectedTarget ? getTargetLabel(selectedTarget) : 'Select target'}
                  </span>
                  <FiChevronDown className={`shrink-0 text-slate-400 transition ${targetDropdownOpen ? 'rotate-180' : ''}`} />
                </button>

                {targetDropdownOpen && (
                  <div className="absolute left-0 right-0 top-full z-30 mt-2 max-h-64 overflow-y-auto rounded-2xl border border-slate-200 bg-white p-2 shadow-xl">
                    {targetOptions.length > 0 ? (
                      <div className="space-y-2">
                        {targetOptions.map((option) => {
                          const optionValue = String(option.id);
                          const isSelected = targetId === optionValue;
                          const optionLabel = getTargetLabel(option);

                          return (
                            <button
                              key={option.id}
                              type="button"
                              title={optionLabel}
                              onClick={() => {
                                setTargetId(optionValue);
                                setTargetDropdownOpen(false);
                              }}
                              className={`flex w-full items-center justify-between gap-3 rounded-xl px-3 py-2.5 text-left text-sm transition ${
                                isSelected
                                  ? 'bg-[#1d4ed8] text-white shadow-sm'
                                  : 'bg-slate-50 text-slate-700 hover:bg-slate-100'
                              }`}
                            >
                              <span className="min-w-0 flex-1 truncate">{optionLabel}</span>
                              {isSelected && <span className="shrink-0 text-xs font-semibold uppercase tracking-[0.16em]">Selected</span>}
                            </button>
                          );
                        })}
                      </div>
                    ) : (
                      <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-3 py-4 text-sm text-slate-500">{getEmptyTargetMessage()}</div>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>

          <div>
            <span className="mb-2 block text-sm font-medium text-slate-600">Reason</span>
            <textarea placeholder="Describe the issue" className="w-full resize-none rounded-2xl border border-slate-200 px-4 py-3 text-sm focus:border-[#1d4ed8] focus:outline-none" rows={5} value={reason} onChange={(e) => setReason(e.target.value)} required />
          </div>

          <button type="submit" className="inline-flex items-center gap-2 rounded-2xl bg-[#1d4ed8] px-4 py-3 font-semibold text-white transition hover:bg-[#1740b8]">
            <FiSend size={18} /> Submit Report
          </button>
        </form>
      </DashboardSection>

      <DashboardSection title="Report History" subtitle="Recent reports submitted from this account">
        {reports.length > 0 ? (
          <div className="space-y-3">
            {reports.map((r) => {
              const booking = bookings.find((b) => b.id === r.targetId);
              const serviceCategory = booking?.service?.category || 'Unnamed Category';
              const serviceSubcategory = booking?.service?.subcategory || 'Unnamed Subcategory';

              const provider = r.targetType === 'PROVIDER' ? bookedProviders?.find((p) => p.id === r.targetId) : null;

              const providerName = provider?.name || provider?.fullName || booking?.provider?.name || booking?.service?.providerName || 'Unknown Provider';
              const date = booking?.bookingDate ? new Date(booking.bookingDate).toLocaleDateString() : 'Unknown Date';
              const status = booking?.status || 'Pending';

              return (
                <div key={r.id} className="rounded-[1.4rem] border border-slate-200 bg-white p-4 shadow-sm">
                  <div className="flex items-center justify-between gap-3">
                    <div className="font-semibold text-slate-900">{r.targetType === 'BOOKING' ? 'Booking Report' : 'Provider Report'}</div>
                    <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em] text-slate-600">{r.status || 'PENDING'}</span>
                  </div>

                  <div className="mt-2 text-sm leading-6 text-slate-600">
                    {r.targetType === 'PROVIDER' && <div>{providerName}</div>}
                    {r.targetType === 'BOOKING' && (
                      <div>
                        {date} - {providerName}
                        <br />
                        {serviceCategory} ({serviceSubcategory})
                        <br />
                        <span className={`text-xs font-semibold ${status === 'COMPLETED' ? 'text-emerald-600' : status === 'CANCELLED' ? 'text-rose-600' : 'text-amber-600'}`}>{status}</span>
                      </div>
                    )}
                  </div>

                  <p className="mt-2 text-sm leading-6 text-slate-600">{r.reason}</p>
                  <div className="mt-2 text-xs text-slate-500">Reported on: {r.createdAt ? new Date(r.createdAt).toLocaleDateString() : 'Unknown Date'}</div>
                  {r.adminResponse && <p className="mt-2 text-xs italic text-slate-600">{r.adminResponse}</p>}
                </div>
              );
            })}
          </div>
        ) : (
          <div className="rounded-[1.4rem] border border-dashed border-slate-300 bg-slate-50 p-5 text-sm text-slate-500">No reports submitted yet.</div>
        )}

        <div className="mt-4 rounded-[1.4rem] border border-slate-200 bg-slate-50 p-4 text-sm text-slate-600">
          <div className="flex items-center gap-2 font-semibold text-slate-900"><FiAlertTriangle className="text-amber-500" /> Useful report targets</div>
          <ul className="mt-3 space-y-2">
            <li>Provider behavior issues</li>
            <li>Problem bookings that need admin attention</li>
            <li>Disputes that should be escalated early</li>
          </ul>
        </div>
      </DashboardSection>
    </div>
  );
}

function DashboardSection({ title, subtitle, action, children }) {
  return (
    <section className="rounded-[1.75rem] border border-white/80 bg-white/90 p-5 shadow-[0_18px_60px_rgba(15,23,42,0.08)]">
      <div className="mb-5 flex items-start justify-between gap-4">
        <div>
          <h2 className="text-xl font-semibold text-slate-900">{title}</h2>
          <p className="mt-1 text-sm text-slate-500">{subtitle}</p>
        </div>
        {action}
      </div>
      {children}
    </section>
  );
}
